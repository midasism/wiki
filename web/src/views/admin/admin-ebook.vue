<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <div>
        <h1>电子书管理</h1>
      </div>


      <!--      编辑对话框-->
      <a-modal
          title="Title"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
      >
        <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="封面">
            <a-input v-model:value="ebook.cover"/>
          </a-form-item>
          <a-form-item label="名称">
            <a-input v-model:value="ebook.name"/>
          </a-form-item>
          <a-form-item label="分类一">
            <a-input v-model:value="ebook.category1Id"/>
          </a-form-item>
          <a-form-item label="分类二">
            <a-input v-model:value="ebook.category2Id"/>
          </a-form-item>
          <a-form-item label="描述">
            <a-input v-model:value="ebook.description" type="textarea"/>
          </a-form-item>
        </a-form>
      </a-modal>

      <p>
        <!--        <a-space :size="40">-->
        <a-button type="primary" size="large" @click="add">
          新增
        </a-button>
        <!--        </a-space>-->
      </p>
      <a-table :columns="columns"
               :data-source="ebooks"
               :row-key="record => record.id"
               :pagination="pagination"
               :loading="Loading"
               @change="handleTableChange"
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
                title="确认要删除这本电子书吗?"
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
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {SmileOutlined, DownOutlined, ExclamationCircleOutlined} from '@ant-design/icons-vue';
import {createVNode, defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message, Modal} from 'ant-design-vue';//全局提示


export default defineComponent({
  name: 'AdminEbook',
  components: {
    SmileOutlined,
    DownOutlined,
    ExclamationCircleOutlined,
  },

  setup() {
    const param = ref();
    param.value = {};
    const ebook = ref()
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: {customRender: 'cover'}
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '分类一',
        dataIndex: 'category1Id'
      },
      {
        title: '分类二',
        dataIndex: 'category2Id'
      },
      {
        title: '文档数',
        dataIndex: 'docCount'
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount'
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
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
      ebook.value = record;
      // ebook.value = JSON.parse(JSON.stringify(record));
    };

    /**
     * 编辑-保存
     **/
    const save = (record: any) => {
      axios.post("/ebook/save", {
        params: record
      });
    }


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
      axios.post("/ebook/save", ebook.value).then((response) => {
        const data = response.data;
        modalLoading.value = false;
        if (data.success) {
          modalVisible.value = false;
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        }else{
          message.error(data.message)
        }
      });
    }

    /**
     * 新增按钮
     **/
    const add = () => {
      modalVisible.value = true;
      ebook.value = {}
    }

    /**
     * 删除
     **/

    const del = (record: any) => {
      axios.delete("/ebook/delete/" + record.id).then((response) => {
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
     * 删除-确认框
     **/
    // const showDelConfirm = (record: any) => {
    //   Modal.confirm({
    //     title: '确定要删除这本电子书吗?',
    //     icon: createVNode(ExclamationCircleOutlined),
    //     // content: 'When clicked the OK button, this dialog will be closed after 1 second',
    //     onOk() {
    //       //调用删除方法
    //       del(record);
    //       return new Promise((resolve, reject) => {
    //         setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
    //       }).catch(() => console.log('Oops errors!'));
    //     },
    //     // eslint-disable-next-line @typescript-eslint/no-empty-function
    //     onCancel() {
    //     },
    //   });
    // }

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      // ebooks.value = [];
      axios.get("/ebook/list", {
        params: params
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          ebooks.value = data.content.list;
          pagination.value.total = data.content.total;

          //重置分页按钮
          pagination.value.current = params.page;
          // pagination.value.total = params.total;
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
        size: pagination.value.pageSize
      });
    });

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,
      edit,
      modalText,
      modalVisible,
      ebook,
      handleOk,
      handleModalOk,
      modalLoading,
      save,

      add,

      del,

      // showDelConfirm
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