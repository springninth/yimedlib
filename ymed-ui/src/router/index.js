import { createRouter, createWebHashHistory } from 'vue-router'; // 改这里
import Login from '../views/Login.vue';
import MainLayout from '../views/MainLayout.vue';
import UserManage from '../views/UserManage.vue';
import CategoryManage from '../views/CategoryManage.vue';
import BookManage from '../views/BookManage.vue';
import BookDetail from '../views/BookDetail.vue';
import BookImageManage from '../views/BookImageManage.vue';

const routes = [
  { path: '/login', component: Login },
  {
    path: '/',  // 根路径用 MainLayout
    component: MainLayout,
    children: [
      { path: '', redirect: '/login' },  // 根路径访问直接跳登录页
      { path: 'users', component: UserManage },
      { path: 'categories', component: CategoryManage },
      { path: 'books', component: BookManage },
      { path: 'books/:id', component: BookDetail },
      { path: 'books/:id/images', component: BookImageManage }
    ]
  }
];

const router = createRouter({
  history: createWebHashHistory(), // 改这里
  routes
});

export default router;
