<template>
  <el-card>
    <div class="header-bar">
      <el-button type="primary" @click="editMode = !editMode">{{ editMode ? '取消编辑' : '编辑' }}</el-button>
      <el-button v-if="editMode" type="success" @click="onSave">保存</el-button>
      <el-button @click="goBack">返回</el-button>
      <el-button type="info" @click="goImages">查看内容图片</el-button>
    </div>
    <el-form :model="book" label-width="120px" :disabled="!editMode" ref="formRef">
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="类目"><el-input :value="book.category ? book.category.name : ''" disabled /></el-form-item>
          <el-form-item label="书名"><el-input v-model="book.title" /></el-form-item>
          <el-form-item label="版本"><el-input v-model="book.edition" /></el-form-item>
          <el-form-item label="著者"><el-input v-model="book.author" /></el-form-item>
          <el-form-item label="收藏地点"><el-input v-model="book.location" /></el-form-item>
          <el-form-item label="方言种类"><el-input v-model="book.dialectType" /></el-form-item>
          <el-form-item label="载体形式"><el-input v-model="book.carrierForm" /></el-form-item>
          <el-form-item label="内容简介"><el-input v-model="book.description" type="textarea" /></el-form-item>
          <el-form-item label="编号"><el-input v-model="book.code" /></el-form-item>
          <el-form-item label="册数"><el-input v-model="book.volumeCount" /></el-form-item>
          <el-form-item label="页数"><el-input v-model="book.pageCount" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发掘者"><el-input v-model="book.discoverer" /></el-form-item>
          <el-form-item label="调查者"><el-input v-model="book.investigator" /></el-form-item>
          <el-form-item label="成书年"><el-input v-model="book.year" /></el-form-item>
          <el-form-item label="抄写者"><el-input v-model="book.copyist" /></el-form-item>
          <el-form-item label="翻译者"><el-input v-model="book.translator" /></el-form-item>
          <el-form-item label="来源地"><el-input v-model="book.sourcePlace" /></el-form-item>
          <el-form-item label="文种"><el-input v-model="book.dialect" /></el-form-item>
          <el-form-item label="古籍类型"><el-input v-model="book.bookType" /></el-form-item>
          <el-form-item label="装帧形式"><el-input v-model="book.bindingForm" /></el-form-item>
          <el-form-item label="页面"><el-input v-model="book.page" /></el-form-item>
          <el-form-item label="纸质"><el-input v-model="book.paper" /></el-form-item>
          <el-form-item label="行款"><el-input v-model="book.lineStyle" /></el-form-item>
          <el-form-item label="破损情况"><el-input v-model="book.damageStatus" /></el-form-item>
          <el-form-item label="翻译整理情况"><el-input v-model="book.translationStatus" /></el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const book = ref({});
const editMode = ref(false);
const formRef = ref();

const fetchBook = async () => {
  const { data } = await api.get(`/books/${route.params.id}`);
  book.value = data || {};
};

const onSave = async () => {
  await api.put('/books', book.value);
  ElMessage.success('保存成功');
  editMode.value = false;
  fetchBook();
};

const goBack = () => router.back();
const goImages = () => router.push(`/books/${route.params.id}/images`);

onMounted(fetchBook);
</script>

<style scoped>
.header-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style> 