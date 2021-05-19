<template>
  <a-layout>
    <a-layout-content style="padding: 0 50px">
      <a-layout style="padding: 24px 0; background: #fff">
        <a-layout-sider width="200" style="background: #fff">
          <a-menu
              mode="inline"
              :default-selected-keys="['1']"
              :defalut-open-keys="['2']"
              style="height: 100%"
          >
            <a-menu-item key="welcome" @click="MenuClick">
              <PieChartOutlined/>
              <span>欢迎</span>
            </a-menu-item>

            <a-sub-menu v-for="c in categoryValue" :key="c.id">
              <template v-slot:title>
                <PieChartOutlined/>
                <span>{{ c.name }}</span>
              </template>

              <a-menu-item v-for="c2 in c.children" :key="c2.id" @click="MenuClick">
                <!--                <PieChartOutlined/>-->
                <span>{{ c2.name }}</span>
              </a-menu-item>
            </a-sub-menu>

          </a-menu>
        </a-layout-sider>
        <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
          <div v-show="isShowWelcome">
            <h1>欢迎</h1>
          </div>
          <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{gutter:20,column:3}"
                  :data-source="eBook">
            <template #renderItem="{ item }">
              <a-list-item key="item.name">
                <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px"/>
            {{ text }}
          </span>
                </template>
                <a-list-item-meta :description="item.description">
                  <template #title>
                    <a :href="item.href">{{ item.name }}</a>
                  </template>
                  <template #avatar>
                    <a-avatar :src="item.cover"/>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>

        </a-layout-content>
      </a-layout>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {StarOutlined, LikeOutlined, MessageOutlined, PieChartOutlined} from '@ant-design/icons-vue';
import {defineComponent, onMounted, ref, reactive, toRef} from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";


export default defineComponent({
  name: 'Home',
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
    PieChartOutlined
  },
  //vue3 初始化调用 组合式API的入口 包含了vue2的生命周期函数
  setup() {
    console.log("setup");
    //vue3 ref 响应式数据
    const eBook = ref();
    const ebooks = ref();
    // const eBook2 = reactive({books: []});
    //分类原始数据
    const categoryData = ref();
    //树型数据
    const categoryValue = ref()

    const openKeys = ref()
    const selectedKeys = ref()

    const isShowWelcome = ref(true)

    const actions: Record<string, string>[] = [
      {type: 'StarOutlined', text: '156'},
      {type: 'LikeOutlined', text: '156'},
      {type: 'MessageOutlined', text: '2'},
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      // loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      // ebooks.value = [];
      axios.get("/ebook/list", {
        params: params
      }).then((response) => {
        // loading.value = false;
        const data = response.data;
        if (data.success) {
          ebooks.value = data.content.list;
          eBook.value = data.content.list;
          // eBook2.books = data.content.list;
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 查询所有分类
     **/
    const CategoryQuery = () => {
      // loading.value = true;
      axios.get("/category/all").then((response) => {
        // loading.value = false;
        const data = response.data;
        if (data.success) {
          categoryData.value = data.content;
          //加载分类树型数据
          categoryValue.value = []
          categoryValue.value = Tool.arrayTree(data.content, 0)
          // 加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
          handleQuery({
            page: 1,
            size: 500,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    const MenuClick = (value: any) => {
      console.log(value)
      isShowWelcome.value = value.key === 'welcome';

      // let tempData = []
      // for (let i = 0; i < ebooks.value.length; i++) {
      //   if (ebooks.value[i].category2Id === category2Id) {
      //     tempData.push(ebooks.value[i])
      //   }
      // }
      // ebooks.value = Tool.copy(tempData)
    }

    onMounted(() => {
      console.log("onMounted");
      CategoryQuery()
      console.log(categoryValue)
    });
    return {
      eBook,
      // books: toRef(eBook2, "books"),
      actions,
      ebooks,
      categoryData,
      categoryValue,

      MenuClick,

      isShowWelcome,
    }
  }
});
</script>

<!--只在当前组件生效-->
<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>