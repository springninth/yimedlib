<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">用户登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onLogin" :loading="loading" style="width:100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/user';
import api from '../api';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref();
const form = ref({ username: '', password: '' });
const loading = ref(false);

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

// 登录方法
const onLogin = () => {
  formRef.value.validate(async valid => {
    if (!valid) return;
    loading.value = true;
    try {
      const {data} = await api.post('/auth/login', form.value);
      if (data.token) {
        // 设置用户信息到 store
        userStore.setUser({token: data.token, username: data.username, role: data.role});
        ElMessage.success('登录成功');
        // 登录成功跳转到 /books
        router.push('/books');
      } else {
        ElMessage.error(data.error || '登录失败');
      }
    } catch (e) {
      ElMessage.error('登录失败');
    } finally {
      loading.value = false;
    }
  });
};

// 页面加载时如果已登录，自动跳转到 /books
onMounted(() => {
  if (userStore.token) {
    router.replace('/books');
  }
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(224, 231, 255, 0) 0%, rgba(240, 253, 250, 0) 100%),
  url("../pict/1.png") center/cover no-repeat;
}

.login-card {
  width: 400px;
  padding: 40px 30px 30px 30px;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  background-color: rgba(255, 255, 255, 0.9);
}

.login-title {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
  font-weight: bold;
  font-size: 22px;
}
</style>
