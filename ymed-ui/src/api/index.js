import axios from 'axios';
import { storeToRefs } from 'pinia';
import { useUserStore } from '../store/user';

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
});

instance.interceptors.request.use(config => {
  // 动态获取 userStore，保证刷新后也能拿到 token
  const userStore = useUserStore();
  const { token } = storeToRefs(userStore);
  if (token.value) {
    config.headers.Authorization = `Bearer ${token.value}`;
  }
  return config;
}, error => Promise.reject(error));

instance.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default instance; 