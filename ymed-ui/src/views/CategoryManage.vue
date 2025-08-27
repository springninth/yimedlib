<template>
  <el-card>
    <div class="header-bar">
      <el-button type="primary" @click="openAddDialog">新增类目</el-button>
    </div>
    <el-table :data="categories" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="类目名称" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCategory(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="类目名称" prop="name">
          <el-input v-model="form.name" autocomplete="off" />
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
import { ref, onMounted } from 'vue';
import api from '../api';
import { ElMessage } from 'element-plus';

const categories = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const form = ref({ id: null, name: '' });
const formRef = ref();
const rules = {
  name: [{ required: true, message: '请输入类目名称', trigger: 'blur' }]
};

const fetchCategories = async () => {
  const { data } = await api.get('/categories');
  categories.value = data;
};

const openAddDialog = () => {
  dialogTitle.value = '新增类目';
  form.value = { id: null, name: '' };
  dialogVisible.value = true;
};

const openEditDialog = row => {
  dialogTitle.value = '编辑类目';
  form.value = { ...row };
  dialogVisible.value = true;
};

const onSubmit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return;
    if (form.value.id) {
      await api.put('/categories', form.value);
      ElMessage.success('修改成功');
    } else {
      await api.post('/categories', form.value);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchCategories();
  });
};

const deleteCategory = async id => {
  await api.delete(`/categories/${id}`);
  ElMessage.success('删除成功');
  fetchCategories();
};

onMounted(fetchCategories);
</script>

<style scoped>
.header-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
</style> 