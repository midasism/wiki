<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <a-menu
        theme="dark"
        mode="horizontal"
        v-model:selectedKeys="selectedKeys1"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="1">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="2" :style="user.id ? {} : {display:'none'}">
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="3" :style="user.id ? {} : {display:'none'}">
        <router-link to="/admin/ebook">电子书管理</router-link>
      </a-menu-item>
      <a-menu-item key="4" :style="user.id ? {} : {display:'none'}">
        <router-link to="/admin/category">分类管理</router-link>
      </a-menu-item>
      <a-menu-item key="5">
        <router-link to="/about">关于我们</router-link>
      </a-menu-item>
      <a-popconfirm
          title="确认要退出登录吗?"
          ok-text="是"
          cancel-text="否"
          @confirm="logout"
      >
        <a class="login-button" v-if="user.loginName">
          <span>退出登录</span>
        </a>
        <a class="login-button" v-if="user.name">
          <span>您好：{{ user.name }}</span>
        </a>
      </a-popconfirm>
      <a class="login-button" @click="loginModel" v-if="!user.loginName">
        <span>登录</span>
      </a>
    </a-menu>

    <a-modal
        title="登录"
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        @ok="login"
    >
      <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName"/>
        </a-form-item>
        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" type="password"/>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import store from "@/store";

declare let hexMd5: any
declare let KEY: any

export default defineComponent({
  name: 'the-header',

  setup() {
    const loginModalVisible = ref(false)
    const loginModalLoading = ref(false)
    //登录成功后从后端接收到的用户信息
    const user = computed(() =>
        store.state.user
    )
    //登录使用
    const loginUser = ref({
      loginName: "",
      password: ""
    });

    /**
     * 登录
     **/
    const login = () => {
      loginModalLoading.value = true;
      //前端校验密码是否合理


      //KEY 盐值  前端第一次加密
      loginUser.value.password = hexMd5(loginUser.value.password + KEY)
      axios.post("/user/login", loginUser.value).then((response) => {
        const data = response.data;
        loginModalLoading.value = false;
        if (data.success) {
          store.commit("setUser", data.content)
          loginModalVisible.value = false;
          loginSuccess()
        } else {
          message.error(data.message)
        }
      });
    }

    /**
     * 退出登录 清空用户信息
     **/
    const logout = () => {
      //将token存入header
      // axios.defaults.headers.common["token"] = store.state.user.token;
      axios.get("/user/logout/" + store.state.user.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登录成功!")
          store.commit("setUser", {})
        } else {
          message.error(data.message)
        }
      });
    }

    /**
     * 显示登录框
     */
    const loginModel = () => {
      loginModalVisible.value = true
    }

    /**
     * 显示登录框
     */
    const loginSuccess = () => {
      message.success('登录成功');
    };


    return {
      loginModalVisible,
      loginModalLoading,
      loginUser,
      user,
      login,
      loginModel,

      logout,
    }
  }
});
</script>

<style>
.login-button {
  float: right;
  color: white;
  padding-left: 20px;
}
</style>