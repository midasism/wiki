<template>
  <a-layout>
    <a-layout-content :style="{ background:'#fff',padding:'24px',margin:0,minHeight:'280px'}">
      <h2 v-if="levelData.length == 0">此电子书无文档！</h2>
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="levelData.length > 0"
              :tree-data="levelData"
              @select="onSelect"
              :replaceFields="{title: 'name',key:'id',value:'id'}"
              :defaultExpandAll="true"
              :defaultSelectedKeys="defaultSelectedKeys"
          ></a-tree>
        </a-col>
        <a-col :span="18">
          <div>
            <h2>{{ doc.name }}</h2>
            <div>
              <span>阅读数：{{ doc.viewCount }}</span>&nbsp;&nbsp;
              <span>点赞数：{{ doc.voteCount }}</span>
            </div>
            <a-divider style="height: 2px;background-color: #9999cc;"/>
          </div>
          <div class="wangeditor" :innerHTML="html"></div>
        </a-col>
      </a-row>
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
import E from 'wangeditor'
//全局提示


export default defineComponent({
  name: 'Doc',
  components: {
    SmileOutlined,
    DownOutlined,
    ExclamationCircleOutlined,
  },

  setup() {
    //路由内置参数 包含路由信息
    const route = useRoute()
    const Docs = ref()
    const html = ref()
    const levelData = ref();
    levelData.value = []//初始化列表 默认是null 调用length会报错
    const defaultSelectedKeys = ref()
    defaultSelectedKeys.value = []
    //当前选中的文档
    const doc = ref()
    doc.value = {}


    /**
     * 文档内容查询
     **/
    const handleQueryContent = (id: Number) => {
      axios.get("/doc/find-content/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          html.value = data.content
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          Docs.value = data.content
          levelData.value = []
          levelData.value = Tool.arrayTree(Docs.value, 0)
          defaultSelectedKeys.value = [levelData.value[0].id]
          handleQueryContent(levelData.value[0].id)
          doc.value = levelData.value[0]
        } else {
          message.error(data.message);
        }
      });
    };

    const QueryViewCount = (doc: any) => {
      axios.get("/doc/queryCount/" + doc.value.id).then((response) => {
        const data = response.data;
        if (data.success) {
          // Docs.value = data.content
          console.log("阅读数：", data)
          doc.value.viewCount = data.content.viewCount
          doc.value.voteCount = data.content.voteCount
        } else {
          message.error(data.message);
        }
      });
    }


    const onSelect = (selectedKeys: any, info: any) => {
      // console.log("selected", selectedKeys, info)
      if (Tool.isNotEmpty(selectedKeys)) {
        //选中文档时，加载文档信息
        doc.value = info.selectedNodes[0].props
        handleQueryContent(selectedKeys[0])
        QueryViewCount(doc)
        console.log("选中文档：", doc.value)
      }
    }


    //初始化数据
    onMounted(() => {
      handleQuery();
    });

    return {
      Docs,
      levelData,
      html,
      onSelect,
      defaultSelectedKeys,
      doc
    }
  }
});
</script>


<style>
/* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
/* table 样式 */
.wangeditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}

.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}

.wangeditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
.wangeditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}

.wangeditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangeditor ul, ol {
  margin: 10px 0 10px 20px;
}

/* 和antdv p冲突，覆盖掉 */
.wangeditor blockquote p {
  font-family: "YouYuan";
  /* !important提高优先级 */
  margin: 20px 10px !important;
  font-size: 16px !important;
  font-weight: 600;
}

/* 点赞 */
.vote-div {
  padding: 15px;
  text-align: center;
}

/* 图片自适应 */
.wangeditor img {
  max-width: 100%;
  height: auto;
}

/* 视频自适应 */
.wangeditor iframe {
  width: 100%;
  height: 400px;
}
</style>