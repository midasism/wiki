<template>
  <a-layout-footer style="text-align: center">
    Midas的电子书
    <div v-if="user.loginName">欢迎：{{ user.name }}</div>
  </a-layout-footer>
</template>

<script lang="ts">
import {computed, defineComponent} from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'the-footer',
  setup() {
    const user = computed(() =>
        store.state.user
    )

    return {
      user
    }
  }
});
</script>