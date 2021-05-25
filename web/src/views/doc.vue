<template>
  <a-layout>
    <a-layout-content :style="{ background:'#fff',padding:'24px',margin:0,minHeight:'280px'}">
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="levelData.length > 0"
              :tree-data="levelData"
              @select="onSelect"
              :replaceFields="{title: 'name',key:'id',value:'id'}"
              :defaultExpandAll="true"
          ></a-tree>
        </a-col>
        <a-col :span="18">

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
    const Docs = ref();
    // Docs.value = []
    const levelData = ref();
    levelData.value = []//初始化列表 默认是null 调用length会报错

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
      Docs,
      levelData
    }
  }
});
</script>