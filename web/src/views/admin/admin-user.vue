<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <div>
        <h1>用户管理</h1>
      </div>


      <!--      编辑对话框-->
      <a-modal
          title="Title"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
      >
        <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="登录名">
            <a-input v-model:value="user.loginName" :disabled="user.id"/>
          </a-form-item>
          <a-form-item label="昵称">
            <a-input v-model:value="user.name"/>
          </a-form-item>
<!--          v-if直接移除 v-show只是简单的隐藏-->
          <a-form-item label="密码" v-show="!user.id">
            <a-input v-model:value="user.password"/>
          </a-form-item>
        </a-form>
      </a-modal>
      <!--      -->


      <!--      重置密码 对话框-->
      <a-modal
          title="Title"
          v-model:visible="resetModalVisible"
          :confirm-loading="resetModalLoading"
          @ok="handleResetModalOk"
      >
        <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="密码">
            <a-input v-model:value="user.password"/>
          </a-form-item>
        </a-form>
      </a-modal>
      <!--      -->


      <!--顶部工具栏：搜索 添加     -->
      <a-form layout="inline" :model="searchParam">
        <a-form-item>
          <a-input v-model:value="searchParam.loginName" placeholder="请输入登录名" @change="SearchChange"
                   @pressEnter="onSearch"/>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" size="large" @click="onSearch">
            搜索
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" size="large" @click="add">
            新增
          </a-button>
        </a-form-item>
      </a-form>
      <!--      -->

      <!--数据表格      -->
      <a-table :columns="columns"
               :data-source="users"
               :row-key="record => record.id"
               :pagination="pagination"
               :loading="Loading"
               @change="handleTableChange"
      >

        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
            <a-popconfirm
                title="确认要删除这个用户吗?"
                ok-text="是"
                cancel-text="否"
                @confirm="del(record)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
      <!--      -->
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {SmileOutlined, DownOutlined, ExclamationCircleOutlined} from '@ant-design/icons-vue';
import {createVNode, defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message, Modal} from 'ant-design-vue';
import {Tool} from "@/util/tool";
//全局提示

declare let hexMd5: any
declare let KEY: any


export default defineComponent({
  name: 'AdminUser',
  components: {
    SmileOutlined,
    DownOutlined,
    ExclamationCircleOutlined,
  },

  setup() {
    const param = ref();
    param.value = {};
    const user = ref()
    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName'
      },
      {
        title: '昵称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];

    /**
     * 编辑按钮
     **/
    const modalText = ref<string>('Content of the modal');
    const modalVisible = ref<boolean>(false);
    const modalLoading = ref<boolean>(false);

    const edit = (record: any) => {
      modalVisible.value = true;
      //user是对话框展示的数据 不直接使用列表的展示数据record 在对话框里的修改不会实时同步到列表
      user.value = Tool.copy(record)
    };

    /**
     * 编辑/新增-确认
     **/
    const handleModalOk = () => {
      modalLoading.value = true;
      //KEY 盐值
      user.value.password = hexMd5(user.value.password + KEY)
      axios.post("/user/save", user.value).then((response) => {
        const data = response.data;
        modalLoading.value = false;
        if (data.success) {
          modalVisible.value = false;
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        } else {
          message.error(data.message)
        }
      });
    }


    /**
     * 重置密码按钮
     **/
    const resetModalVisible = ref<boolean>(false);
    const resetModalLoading = ref<boolean>(false);

    const resetPassword = (record: any) => {
      resetModalVisible.value = true;
      //user是对话框展示的数据 不直接使用列表的展示数据record 在对话框里的修改不会实时同步到列表
      user.value = Tool.copy(record)
      user.value.password = null
    };

    /**
     * 重置密码 确认
     **/
    const handleResetModalOk = () => {
      resetModalLoading.value = true;
      //KEY 盐值
      user.value.password = hexMd5(user.value.password + KEY)
      axios.post("/user/reset-password", user.value).then((response) => {
        const data = response.data;
        resetModalLoading.value = false;
        if (data.success) {
          resetModalVisible.value = false;
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        } else {
          message.error(data.message)
        }
      });
    }


    /**
     * 新增按钮
     **/
    const add = () => {
      modalVisible.value = true;
      user.value = {}
    }

    /**
     * 删除
     **/
    const del = (record: any) => {
      axios.delete("/user/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          modalLoading.value = false;

          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        }
      });
    }


    /**
     * 搜索
     **/
    const inputValue = ref<string>('');
    const searchParam = ref({
      loginName: ""
    })

    const onSearch = () => {
      //搜索参数
      let param = ""
      if (searchParam.value.loginName != "") {
        param += (param == "" ? "?" : "&")
        param += ("loginName=" + searchParam.value.loginName)
      }
      axios.get("/user/list" + param).then((response) => {
        users.value = response.data.content.list;
      });
    };

    /**
     * 搜索框内容改变触发
     **/
    const SearchChange = () => {
      //搜索框内容为空 重新查询并展示所有数据
      if (searchParam.value.loginName == "") {
        axios.get("/user/list").then((response) => {
          users.value = response.data.content.list;
        });
      }
    };


    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      axios.get("/user/list", {
        params: params
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          users.value = data.content.list;
          pagination.value.total = data.content.total;

          //重置分页按钮
          pagination.value.current = params.page;
        } else {
          message.error(data.message);
        }
      });
    };


    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    //初始化数据
    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      edit,
      modalText,
      modalVisible,
      user,
      handleModalOk,
      modalLoading,

      add,

      del,


      searchParam,
      inputValue,
      onSearch,
      SearchChange,

      resetModalVisible,
      resetModalLoading,
      resetPassword,
      handleResetModalOk,

    }
  }
});
</script>

<!--只在当前组件生效-->
<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>