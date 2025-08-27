<template>
  <el-card>
    <div class="header-bar">
      <el-button type="primary" @click="openAddDialog">新增用户</el-button>
    </div>
    <el-table :data="users" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
          </el-select>
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

const users = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const form = ref({ id: null, username: '', password: '', role: 'ADMIN' });
const formRef = ref();
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
};

const fetchUsers = async () => {
  const { data } = await api.get('/users');
  users.value = data;
};

const openAddDialog = () => {
  dialogTitle.value = '新增用户';
  form.value = { id: null, username: '', password: '', role: 'ADMIN' };
  dialogVisible.value = true;
};

const openEditDialog = row => {
  dialogTitle.value = '编辑用户';
  form.value = { ...row, password: '' };
  dialogVisible.value = true;
};

const onSubmit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return;
    if (form.value.id) {
      // 编辑
      await api.put('/users', form.value);
      ElMessage.success('修改成功');
    } else {
      // 新增
      await api.post('/users', form.value);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchUsers();
  });
};

const deleteUser = async id => {
  await api.delete(`/users/${id}`);
  ElMessage.success('删除成功');
  fetchUsers();
};

onMounted(fetchUsers);
</script>

<style scoped>
.header-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
</style> 