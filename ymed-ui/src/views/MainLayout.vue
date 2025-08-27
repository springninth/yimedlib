<template>
  <el-container style="height:100vh">
    <el-aside width="220px" class="sidebar">
      <el-menu :default-active="activeCategory" @select="onCategorySelect" class="category-menu">
        <el-menu-item v-for="cat in categories" :key="cat" :index="cat">{{ cat }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">中国彝医药珍善本古籍数字资源库</div>
        <div class="header-center">
          <el-input v-model="search" placeholder="搜索古籍（任意字段）" style="width: 320px" @keyup.enter="onSearch" clearable />
          <el-button type="primary" @click="onSearch" style="margin-left: 8px">搜索</el-button>
        </div>
        <div class="header-right">
          <span>{{ userStore.username }}</span>
          <el-dropdown>
            <span class="el-dropdown-link">
              管理 <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goUserManage">用户管理</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view :key="activeCategory" v-slot="{ Component }">
          <component :is="Component" :category="activeCategory" :search="searchKey" />
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';
import { ArrowDown } from '@element-plus/icons-vue';

const categories = [
  '医经', '医理', '诊治', '本草', '病症用药', '调护', '医史', '作祭献药', '医算', '综合'
];
const activeCategory = ref(categories[0]);
const search = ref('');
const searchKey = ref('');
const userStore = useUserStore();
const router = useRouter();

const onCategorySelect = cat => {
  activeCategory.value = cat;
  searchKey.value = '';
  if (router.currentRoute.value.path !== '/books') {
    router.push('/books');
  }
};
const onSearch = () => {
  searchKey.value = search.value;
};
const goUserManage = () => {
  router.push('/users');
};
const logout = () => {
  userStore.logout();
  router.push('/login');
};

watch(() => userStore.token, token => {
  if (!token) router.push('/login');
});
</script>

<style scoped>
/* 左侧边栏背景图 */
.sidebar {
  background: url("../pict/4.png") top center/cover no-repeat, #f5f7fa;
  border-right: 1px solid #e4e7ed;
}

/* 左侧菜单样式优化 - 使用更具体的选择器避免全局影响 */
.sidebar .category-menu {
  margin-top: 24px;
  background: transparent;
}

/* 去掉菜单项的白色背景，使用透明背景 - 限制作用域 */
.sidebar .category-menu .el-menu-item {
  background: transparent;
  color: #333;
  font-weight: bold; /* 加粗字体 */
  font-size: 16px; /* 增大字体大小 */
  margin: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

/* 菜单项悬停效果 - 限制作用域 */
.sidebar .category-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.3);
  color: #409eff;
}

/* 选中状态的菜单项 - 限制作用域 */
.sidebar .category-menu .el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.15);
  color: #409eff;
  font-weight: bold;
}

/* 顶部横向栏背景图片 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: url("../pict/2.png") center/cover no-repeat;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  height: 60px;
}

/* 顶部标题样式（白字 + 半透明底 + 阴影） */
.header-left {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.4);
  padding: 4px 8px;
  border-radius: 6px;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.8);
}

.header-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 主内容区背景优化 - 增加透明度降低背景图的显示强度 */
.main-content {
  background: linear-gradient(rgba(244, 248, 244, 0.7), rgba(244, 248, 244, 0.7)), url("../pict/3.png") center/cover no-repeat;
  min-height: calc(100vh - 60px);
  padding: 24px;
}
</style>