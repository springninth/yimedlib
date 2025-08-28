<template>
  <el-card>
    <!-- 统计卡片 -->
    <el-card class="stats-card" style="margin-bottom: 16px;">
      <template #header>
        <div class="stats-header">
          <span>古籍分类统计</span>
          <el-button type="text" size="small" @click="fetchCategoryStats" :loading="statsLoading">
            <el-icon><refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-grid">
          <div
              v-for="(count, categoryName) in categoryStats"
              :key="categoryName"
              class="stat-item"
              :class="{ 'active': categoryName === category }"
              @click="selectCategory(categoryName)"
          >
            <div class="stat-name">{{ categoryName }}</div>
            <div class="stat-count">{{ count }}本</div>
          </div>
        </div>
        <div class="total-stats">
          <strong>总计：{{ totalBooks }}本古籍</strong>
        </div>
      </div>
    </el-card>

    <!-- 操作栏 -->
    <div class="header-bar">
      <el-button type="primary" @click="openAddDialog">新增古籍</el-button>
      <el-select v-model="pageSize" style="width: 120px; margin-left: 16px" @change="fetchBooks">
        <el-option :value="10" label="每页10条" />
        <el-option :value="20" label="每页20条" />
        <el-option :value="50" label="每页50条" />
      </el-select>
      <div style="margin-left: 16px; color: #666; font-size: 14px;">
        当前分类：<strong>{{ category }}</strong> ({{ currentCategoryCount }}本)
      </div>
    </div>

    <!-- 书籍表格 -->
    <el-table :data="books" style="width: 100%; margin-top: 16px" @row-click="goDetail">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="编号" width="120" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="著者" />
      <el-table-column prop="edition" label="版本" />
      <el-table-column prop="location" label="收藏地点" />
      <el-table-column prop="category.name" label="类目" />
      <el-table-column label="操作" width="240" align="center">
        <template #default="scope">
          <div class="action-buttons-grid">
            <el-button size="small" type="default" @click.stop="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="default" @click.stop="goDetail(scope.row)">详情</el-button>
            <el-button size="small" type="default" @click.stop="goImages(scope.row)">内容</el-button>
            <el-button size="small" type="danger" @click.stop="deleteBook(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, jumper"
        style="margin-top: 16px; text-align: right"
        @current-change="fetchBooks"
    />
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="form.edition" />
        </el-form-item>
        <el-form-item label="著者">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="收藏地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="类目" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择类目">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容简介">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="方言种类">
          <el-input v-model="form.dialectType" />
        </el-form-item>
        <el-form-item label="载体形式">
          <el-input v-model="form.carrierForm" />
        </el-form-item>
        <el-form-item label="编号" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="册数">
          <el-input v-model="form.volumeCount" />
        </el-form-item>
        <el-form-item label="页数">
          <el-input v-model="form.pageCount" />
        </el-form-item>
        <el-form-item label="发掘者">
          <el-input v-model="form.discoverer" />
        </el-form-item>
        <el-form-item label="调查者">
          <el-input v-model="form.investigator" />
        </el-form-item>
        <el-form-item label="成书年">
          <el-input v-model="form.year" />
        </el-form-item>
        <el-form-item label="抄写者">
          <el-input v-model="form.copyist" />
        </el-form-item>
        <el-form-item label="翻译者">
          <el-input v-model="form.translator" />
        </el-form-item>
        <el-form-item label="来源地">
          <el-input v-model="form.sourcePlace" />
        </el-form-item>
        <el-form-item label="文种">
          <el-input v-model="form.dialect" />
        </el-form-item>
        <el-form-item label="古籍类">
          <el-input v-model="form.bookType" />
        </el-form-item>
        <el-form-item label="装帧形式">
          <el-input v-model="form.bindingForm" />
        </el-form-item>
        <el-form-item label="页面">
          <el-input v-model="form.page" />
        </el-form-item>
        <el-form-item label="纸质">
          <el-input v-model="form.paper" />
        </el-form-item>
        <el-form-item label="行款">
          <el-input v-model="form.lineStyle" />
        </el-form-item>
        <el-form-item label="破损情况">
          <el-input v-model="form.damageStatus" />
        </el-form-item>
        <el-form-item label="翻译整理情况">
          <el-input v-model="form.translationStatus" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, watch, nextTick, computed } from 'vue';
import api from '../api';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { toRefs } from 'vue';
import { Refresh } from '@element-plus/icons-vue';

const props = defineProps({
  category: String,
  search: String
});
const { category, search } = toRefs(props);
const emit = defineEmits(['categorySelect']);

const books = ref([]);
const categories = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const form = ref({
  id: null,
  title: '',
  edition: '',
  author: '',
  location: '',
  categoryId: '',
  description: '',
  dialectType: '',
  carrierForm: '',
  code: '',
  volumeCount: '',
  pageCount: '',
  discoverer: '',
  investigator: '',
  year: '',
  copyist: '',
  translator: '',
  sourcePlace: '',
  dialect: '',
  bookType: '',
  bindingForm: '',
  page: '',
  paper: '',
  lineStyle: '',
  damageStatus: '',
  translationStatus: ''
});
const formRef = ref();
const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择类目', trigger: 'change' }]
};
const router = useRouter();

// 统计相关
const categoryStats = ref({});
const statsLoading = ref(false);

// 计算当前分类的书籍数量
const currentCategoryCount = computed(() => {
  return categoryStats.value[category.value] || 0;
});

// 计算总书籍数
const totalBooks = computed(() => {
  return Object.values(categoryStats.value).reduce((total, count) => total + count, 0);
});

// 获取分类统计数据
const fetchCategoryStats = async () => {
  statsLoading.value = true;
  try {
    const { data } = await api.get('/categories/stats');
    categoryStats.value = data;
  } catch (error) {
    console.error('获取统计数据失败:', error);
    ElMessage.error('获取统计数据失败');
  } finally {
    statsLoading.value = false;
  }
};

// 选择分类
const selectCategory = (categoryName) => {
  // 通知父组件切换分类
  emit('categorySelect', categoryName);
  // 直接更新当前分类（用于点击统计卡片切换分类）
  if (router.currentRoute.value.path === '/books') {
    // 如果已经在书籍页面，直接更新
    // 这里需要父组件配合处理
    window.dispatchEvent(new CustomEvent('categoryChange', { detail: categoryName }));
  }
};

const fetchCategories = async () => {
  const { data } = await api.get('/categories');
  categories.value = Array.isArray(data) ? data : [];
};

const fetchBooks = async () => {
  if (!Array.isArray(categories.value) || categories.value.length === 0) return;
  let url = `/books/category/${getCategoryId()}?page=${page.value - 1}&size=${pageSize.value}`;
  if (search.value) {
    url = `/books/search?keyword=${search.value}&page=${page.value - 1}&size=${pageSize.value}`;
  }
  const { data } = await api.get(url);
  books.value = data.content || [];
  total.value = data.totalElements || 0;
};

const getCategoryId = () => {
  if (!Array.isArray(categories.value)) return '';
  const cat = categories.value.find(c => c.name === category.value);
  return cat ? cat.id : '';
};

const openAddDialog = async () => {
  if (!Array.isArray(categories.value) || categories.value.length === 0) {
    await fetchCategories();
    if (!Array.isArray(categories.value) || categories.value.length === 0) {
      ElMessage.error('类目数据未加载，请稍后重试');
      return;
    }
  }
  dialogTitle.value = '新增古籍';
  form.value = {
    id: null,
    title: '',
    edition: '',
    author: '',
    location: '',
    categoryId: getCategoryId(),
    description: '',
    dialectType: '',
    carrierForm: '',
    code: '',
    volumeCount: '',
    pageCount: '',
    discoverer: '',
    investigator: '',
    year: '',
    copyist: '',
    translator: '',
    sourcePlace: '',
    dialect: '',
    bookType: '',
    bindingForm: '',
    page: '',
    paper: '',
    lineStyle: '',
    damageStatus: '',
    translationStatus: ''
  };
  dialogVisible.value = true;
};

const openEditDialog = row => {
  dialogTitle.value = '编辑古籍';
  form.value = { ...row, categoryId: row.category?.id };
  dialogVisible.value = true;
};

const onSubmit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return;
    const payload = { ...form.value, category: { id: form.value.categoryId } };
    if (form.value.id) {
      await api.put('/books', payload);
      ElMessage.success('修改成功');
    } else {
      await api.post('/books', payload);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchBooks();
    // 新增或修改后刷新统计
    fetchCategoryStats();
  });
};

const deleteBook = async id => {
  await api.delete(`/books/${id}`);
  ElMessage.success('删除成功');
  fetchBooks();
  // 删除后刷新统计
  fetchCategoryStats();
};

const goDetail = row => {
  router.push(`/books/${row.id}`);
};
const goImages = row => {
  router.push(`/books/${row.id}/images`);
};

// 修复：每次切换类目、搜索时都能正确查询
watch([category, search], async () => {
  page.value = 1;
  await nextTick();
  if (!Array.isArray(categories.value) || categories.value.length === 0) {
    await fetchCategories();
  }
  fetchBooks();
});

// 初次进入页面自动查询
onMounted(async () => {
  await fetchCategories();
  await fetchCategoryStats();
  fetchBooks();
});

// 类目切换时跳转
watch(category, (newCat, oldCat) => {
  if (window.location.pathname !== '/books') {
    router.push('/books');
  }
});

// pageSize 改变刷新
watch(pageSize, async () => {
  page.value = 1;
  await nextTick();
  if (!Array.isArray(categories.value) || categories.value.length === 0) {
    await fetchCategories();
  }
  fetchBooks();
});
</script>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

/* 统计卡片样式 */
.stats-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-content {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 12px;
    margin-bottom: 16px;
  }

  .stat-item {
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid #e9ecef;
    border-radius: 8px;
    padding: 12px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);

    &:hover {
      background: rgba(227, 242, 253, 0.9);
      border-color: #409eff;
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    }

    &.active {
      background: linear-gradient(135deg, #409eff, #67c23a);
      color: white;
      border-color: #409eff;
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
    }

    .stat-name {
      font-size: 13px;
      margin-bottom: 4px;
      font-weight: 500;
    }

    .stat-count {
      font-size: 16px;
      font-weight: bold;
    }
  }

  .total-stats {
    text-align: center;
    padding: 12px;
    background: rgba(240, 249, 255, 0.9);
    border-radius: 8px;
    color: #0369a1;
    border: 1px solid rgba(3, 105, 161, 0.2);
  }
}

/* 两行两列布局的操作按钮 */
.action-buttons-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px 8px; /* 行间距6px，列间距8px */
  justify-items: center;
  align-items: center;
}

/* 按钮紧凑美观 */
.action-buttons-grid .el-button {
  width: 100%;
  min-width: 0;
  padding: 4px 0;
  font-size: 12px;
  border-radius: 4px;
}

/* 按钮悬停效果 */
.action-buttons-grid .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: all 0.2s ease;
}
</style>