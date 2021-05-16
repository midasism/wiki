<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <div>
        <h1>电子书管理</h1>
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'AdminEbook',
  components: {},
  //vue3 初始化调用 组合式API的入口 包含了vue2的生命周期函数
  setup() {
    console.log("setup");

    return {}
  }
});
</script>

<!--只在当前组件生效-->
<style scoped>

</style>