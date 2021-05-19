<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <div>
        <h1>文档管理</h1>
      </div>


      <!--      编辑对话框-->
      <a-modal
          title="Title"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
      >
        <a-form :model="Doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="名称">
            <a-input v-model:value="Doc.name"/>
          </a-form-item>
          <a-form-item label="父文档">
            <a-tree-select
                v-model:value="Doc.parent"
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :tree-data="tempLevelData"
                placeholder="请选择父文档"
                tree-default-expand-all
                :replaceFields="{title:'name',key:'id',value:'id'}"
            >
            </a-tree-select>
          </a-form-item>
<!--          <a-form-item label="父文档">-->
<!--            <a-select-->
<!--                v-model:value="Doc.parent"-->
<!--            >-->
<!--              <a-select-option value="0">无</a-select-option>-->
<!--              <a-select-option v-for="c in levelData" :key="c.id" :value="c.id" :disabled="Doc.id === c.id">{{-->
<!--                  c.name-->
<!--                }}-->
<!--              </a-select-option>-->
<!--            </a-select>-->
<!--          </a-form-item>-->
          <a-form-item label="顺序">
            <a-input v-model:value="Doc.sort"/>
          </a-form-item>
        </a-form>
      </a-modal>
      <!--      -->


      <!--顶部工具栏：搜索 添加     -->
      <a-form layout="inline" :model="searchParam">
        <a-form-item>
          <a-input v-model:value="searchParam.name" placeholder="请输入文档名称" @change="SearchChange"
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
                title="确认要删除这个文档吗?"
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
  name: 'AdminDoc',
  components: {
    SmileOutlined,
    DownOutlined,
    ExclamationCircleOutlined,
  },

  setup() {
    const param = ref();
    param.value = {};
    const Doc = ref()
    const Docs = ref();
    const levelData = ref();
    //编辑时显示的树型数据 剔除了自身节点和子节点 防止形成引用循环 导致节点从树中脱离
    const tempLevelData = ref();
    tempLevelData.value = []
    const loading = ref(false);

    const columns = [
      {
        title: '父文档',
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
      //Doc是对话框展示的数据 不直接使用列表的展示数据record 在对话框里的修改不会实时同步到列表
      tempLevelData.value = Tool.copy(levelData.value)
      Doc.value = Tool.copy(record)
      setDisable(tempLevelData.value, record.id)
      //最前面增加 无（根节点）
      tempLevelData.value.unshift({id: 0, name: '无'})
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
      axios.post("/doc/save", Doc.value).then((response) => {
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
      Doc.value = {}
      tempLevelData.value = Tool.copy(levelData.value)
      //最前面增加 无（根节点）
      tempLevelData.value.unshift({id: 0, name: '无'})
    }

    /**
     * 删除
     **/
    const del = (record: any) => {
      axios.delete("/doc/delete/" + record.id).then((response) => {
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
      axios.get("/doc/list" + param).then((response) => {
        Docs.value = response.data.content;
      });
    };

    /**
     * 搜索框内容改变触发
     **/
    const SearchChange = () => {
      //搜索框内容为空 重新查询并展示所有数据
      if (searchParam.value.name == "") {
        axios.get("/doc/list").then((response) => {
          Docs.value = response.data.content;
        });
      }
    };


    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          Docs.value = data.content
          console.log(Docs.value)
          levelData.value = []
          levelData.value = Tool.arrayTree(Docs.value, 0)
          console.log(levelData)
        } else {
          message.error(data.message);
        }
      });
    };

    const setDisable = (treeSelectData: any, id: any) => {
      for (let i = 0; i < treeSelectData.length; i++) {
        const data = treeSelectData[i];
        if (data.id === id) {
          data.disabled = true;

          const children = data.children;
          if (Tool.isNotEmpty(children)) {
            for (let k = 0; k < children.length; k++) {
              setDisable(children, children[k].id)
            }
          }
        } else {
          const children = data.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id)
          }
        }
      }
    }

    //初始化数据
    onMounted(() => {
      handleQuery();
    });

    return {
      Docs,
      columns,
      loading,
      edit,
      modalText,
      modalVisible,
      Doc,
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

      tempLevelData,
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