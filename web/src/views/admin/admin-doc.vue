<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <div>
        <h1>文档管理</h1>
      </div>
      <a-row :gutter="24">
        <a-col :span="8">
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
          <a-table
              v-if="levelData.length > 0"
              :columns="columns"
              :data-source="levelData"
              :row-key="record => record.id"
              :loading="Loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text,record }">
              {{ record.sort }} {{ text }}
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
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">保存</a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="Doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" layout="vertical">
            <a-form-item label="名称">
              <a-input v-model:value="Doc.name" placeholder="请输入文档名称"/>
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
            <a-form-item label="顺序">
              <a-input v-model:value="Doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="previewContent()">
                <template #icon>
                  <SearchOutlined/>
                </template>
                预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>

          </a-form>

        </a-col>
      </a-row>

      <a-drawer
          placement="right"
          :closable="false"
          v-model:visible="previewVisible"
          @close="previewClose"
          width="900"
      >
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {SmileOutlined, DownOutlined, ExclamationCircleOutlined} from '@ant-design/icons-vue';
import {createVNode, defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message, Modal} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
// import E from 'wangeditor'
import WangEditor from 'wangeditor';
//全局提示


export default defineComponent({
  name: 'AdminDoc',
  components: {
    SmileOutlined,
    DownOutlined,
    ExclamationCircleOutlined,
  },

  setup() {
    //路由内置参数 包含路由信息
    const route = useRoute()
    console.log(route.query.ebookId)
    const param = ref();
    param.value = {};
    const Doc = ref();
    Doc.value = {
      name: "",
      parent: 0,
      sort: 1,
      content: ""
    }
    const Docs = ref();
    const levelData = ref();
    levelData.value = []//初始化列表 默认是null 调用length会报错
    //编辑时显示的树型数据 剔除了自身节点和子节点 防止形成引用循环 导致节点从树中脱离
    const tempLevelData = ref();
    tempLevelData.value = []
    const loading = ref(false);
    //wangEditor
    var editor: WangEditor
    // const editor = new WangEditor("#content")
    const columns = [
      // {
      //   title: '父文档',
      //   dataIndex: 'parent'
      // },
      {
        title: '名称',
        dataIndex: 'name',
        slots: {customRender: 'name'}
      },
      // {
      //   title: '排序',
      //   dataIndex: 'sort'
      // },
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
      //查询文档内容
      handleQueryContent()
      setDisable(tempLevelData.value, record.id)
      //最前面增加 无（根节点）
      tempLevelData.value.unshift({id: 0, name: '无'})

      editor.create();
      //清空富文本框
      editor.txt.html("")
    };

    /**
     * 编辑/新增-确认
     **/
    const handleSave = () => {
      // modalLoading.value = true;
      Doc.value.ebookId = route.query.ebookId
      Doc.value.content = editor.txt.html()
      axios.post("/doc/save", Doc.value).then((response) => {
        const data = response.data;
        modalLoading.value = false;
        if (data.success) {
          //成功提示框
          message.success('保存成功');
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
      editor.txt.html("")
      tempLevelData.value = Tool.copy(levelData.value)
      //最前面增加 无（根节点）
      tempLevelData.value.unshift({id: 0, name: '无'})
    }

    /**
     * 删除
     **/
    let deleteIds: Array<String> = []
    let AllDocNames = ""

    const del = (record: any) => {
      getDeleteIds(levelData.value, record.id)

      if (deleteIds.length > 1) {
        doubleCheckDelete(deleteIds, record)
        return
      }

      axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          modalLoading.value = false;

          //重新加载列表
          handleQuery();
          //清空待删除文档名和文档id
          AllDocNames = ""
          deleteIds = []
        }
      });
    }

    /**
     * 递归获取待删除节点及子节点的id
     **/
    const getDeleteIds = (treeSelectData: any, id: any) => {
      for (let i = 0; i < treeSelectData.length; i++) {
        const data = treeSelectData[i];
        if (data.id == id) {
          deleteIds.push(data.id);
          AllDocNames += (data.name + " ");
          const children = data.children;
          if (Tool.isNotEmpty(children)) {
            for (let k = 0; k < children.length; k++) {
              getDeleteIds(children, children[k].id)
            }
          }
        } else {
          const children = data.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id)
          }
        }
      }
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
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          Docs.value = data.content
          levelData.value = []
          levelData.value = Tool.arrayTree(Docs.value, 0)
          //页面刚加载出来时就加载父文档结构，节省点击新增按钮的操作
          tempLevelData.value = Tool.copy(levelData.value)
          //最前面增加 无（根节点）
          tempLevelData.value.unshift({id: 0, name: '无'})
        } else {
          message.error(data.message);
        }
      });
    };


    /**
     * 内容查询
     **/
    const handleQueryContent = () => {
      axios.get("/doc/find-content/" + Doc.value.id).then((response) => {
        const data = response.data;
        if (data.success) {
          //将查询到的内容放到富文本框
          editor.txt.html(data.content)
        } else {
          message.error(data.message);
        }
      });
    };


    /**
     * 递归设置当前文档及子文档为不可选 防止一个文档选择自己子文档为父文档，造成引用循环
     **/
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

    /**
     * 递归获取待删除节点及子节点的id
     **/


    const doubleCheckDelete = (deleteIds: Array<String>, record: any) => {
      console.log(AllDocNames)
      Modal.confirm({
        title: '你想要删除本文档及所有子文档吗？',
        icon: createVNode(ExclamationCircleOutlined),
        content: AllDocNames,
        onOk() {
          return new Promise((resolve, reject) => {
            axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
              const data = response.data;
              if (data.success) {
                modalVisible.value = false;
                modalLoading.value = false;
                //重新加载列表
                handleQuery();
                //清空待删除文档名和文档id
                AllDocNames = ""
                deleteIds = []
              }
            });
            setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
          }).catch(() => console.log('Oops errors!'));
        },
      });
    };


    //预览文档
    const previewHtml = ref()
    const previewVisible = ref(false)
    /**
     * 点击预览按钮进行文档预览
     */
    const previewContent = () => {
      const html = editor.txt.html()
      previewHtml.value = html
      previewVisible.value = true
    };

    const previewClose = () => {
      previewVisible.value = false
    };


    //初始化数据
    onMounted(() => {
      handleQuery();

      // const editor = new E('#content')
      editor = new WangEditor("#content")
      editor.config.zIndex = 0;
      editor.create();
    });

    return {
      Docs,
      columns,
      loading,
      edit,
      modalText,
      modalVisible,
      Doc,
      handleSave,
      modalLoading,
      add,
      del,

      searchParam,
      inputValue,
      onSearch,
      SearchChange,

      levelData,

      tempLevelData,

      previewContent,
      previewHtml,
      previewVisible,
      previewClose,

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