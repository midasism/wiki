<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <div>
        <h1>分类管理</h1>
      </div>


      <!--      编辑对话框-->
      <a-modal
          title="Title"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
      >
        <a-form :model="Category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="名称">
            <a-input v-model:value="Category.name"/>
          </a-form-item>
          <a-form-item label="父分类">
            <a-select
                v-model:value="Category.parent"
            >
              <a-select-option value="0">无</a-select-option>
              <a-select-option v-for="c in levelData" :key="c.id" :value="c.id" :disabled="Category.id === c.id">{{ c.name }}</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="顺序">
            <a-input v-model:value="Category.sort"/>
          </a-form-item>
        </a-form>
      </a-modal>
      <!--      -->


      <!--顶部工具栏：搜索 添加     -->
      <a-form layout="inline" :model="searchParam">
        <a-form-item>
          <a-input v-model:value="searchParam.name" placeholder="请输入分类名称" @change="SearchChange"
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
      <!-- 只要数据有children属性，是递归结构，表格会自动转化为树型表格     -->
      <a-table :columns="columns"
               :data-source="levelData"
               :row-key="record => record.id"
               :loading="Loading"
               :pagination="false"
      >
        <template #cover="{ text:cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>

        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="确认要删除这个分类吗?"
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


export default defineComponent({
  name: 'AdminCategory',
  components: {
    SmileOutlined,
    DownOutlined,
    ExclamationCircleOutlined,
  },

  setup() {
    const param = ref();
    param.value = {};
    const Category = ref()
    const Categorys = ref();
    const levelData = ref();
    const loading = ref(false);

    const columns = [
      {
        title: '父分类',
        dataIndex: 'parent'
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '排序',
        dataIndex: 'sort'
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
      //Category是对话框展示的数据 不直接使用列表的展示数据record 在对话框里的修改不会实时同步到列表
      Category.value = Tool.copy(record)
    };


    /**
     * 模拟确认
     **/
    const handleOk = () => {
      modalLoading.value = true;
      setTimeout(() => {
        modalVisible.value = false;
        modalLoading.value = false;
      }, 2000);
    };

    /**
     * 编辑/新增-确认
     **/
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/category/save", Category.value).then((response) => {
        const data = response.data;
        modalLoading.value = false;
        if (data.success) {
          modalVisible.value = false;
          //重新加载列表
          handleQuery();
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
      Category.value = {}
    }

    /**
     * 删除
     **/

    const del = (record: any) => {
      axios.delete("/category/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          modalLoading.value = false;

          //重新加载列表
          handleQuery();
        }
      });
    }


    /**
     * 搜索
     **/
    const inputValue = ref<string>('');
    const searchParam = ref({
      name: ""
    })

    const onSearch = () => {
      //搜索参数
      let param = ""
      if (searchParam.value.name != "") {
        param += (param == "" ? "?" : "&")
        param += ("name=" + searchParam.value.name)
      }
      axios.get("/category/list" + param).then((response) => {
        Categorys.value = response.data.content;
      });
    };

    /**
     * 搜索框内容改变触发
     **/
    const SearchChange = () => {
      //搜索框内容为空 重新查询并展示所有数据
      if (searchParam.value.name == "") {
        axios.get("/category/list").then((response) => {
          Categorys.value = response.data.content;
        });
      }
    };


    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      // Categorys.value = [];
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          Categorys.value = data.content
          console.log(Categorys.value)
          levelData.value = []
          levelData.value = Tool.arrayTree(Categorys.value, 0)
          console.log(levelData)
        } else {
          message.error(data.message);
        }
      });
    };

    //初始化数据
    onMounted(() => {
      handleQuery();
    });

    return {
      Categorys,
      columns,
      loading,
      edit,
      modalText,
      modalVisible,
      Category,
      handleOk,
      handleModalOk,
      modalLoading,

      add,

      del,

      searchParam,
      inputValue,
      onSearch,
      SearchChange,

      levelData,
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