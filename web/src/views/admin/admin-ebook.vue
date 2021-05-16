<template>
  <div>
    电子书管理
  </div>
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