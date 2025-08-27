<template>
  <el-card>
    <div class="header-bar">
      <!-- 自定义文件上传，不使用element-plus的action自动上传 -->
      <el-upload
          :auto-upload="false"
          :multiple="true"
          :show-file-list="false"
          :on-change="handleFileChange"
          drag
          class="upload-area"
          accept="image/*"
      >
        <el-icon><upload-filled /></el-icon>
        <div class="el-upload__text">拖拽或点击上传图片</div>
        <div class="el-upload__tip">图片将按文件名排序上传</div>
      </el-upload>

      <!-- 手动上传按钮 -->
      <el-button
          type="primary"
          @click="uploadFiles"
          :disabled="fileList.length === 0"
          :loading="uploading"
          style="margin-left: 16px"
      >
        {{ uploading ? '上传中...' : `上传 ${fileList.length} 个文件` }}
      </el-button>

      <el-button style="margin-left: 8px" @click="goBack">返回</el-button>
    </div>

    <!-- 显示待上传文件列表 -->
    <div v-if="fileList.length > 0" class="file-preview">
      <h4>待上传文件 (按文件名排序):</h4>
      <el-tag
          v-for="(file, index) in sortedFileList"
          :key="index"
          style="margin-right: 8px; margin-bottom: 8px"
      >
        {{ file.name }}
      </el-tag>
      <el-button size="small" type="danger" @click="clearFiles">清空</el-button>
    </div>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col v-for="(img, idx) in images" :key="img.id" :span="6">
        <el-card :body-style="{ padding: '8px' }" class="img-card">
          <img
              :src="getFullImageUrl(img.imageUrl)"
              alt="图片"
              class="book-img"
              @click="openViewer(idx)"
              style="cursor:pointer"
          />

          <!-- 显示图片信息 -->
          <div class="image-info">
            <div class="image-filename">{{ getFileName(img.imageUrl) }}</div>
            <div class="image-order">顺序: {{ img.imgOrder + 1 }}</div>
          </div>

          <div style="text-align: right; margin-top: 8px">
            <el-button size="small" type="danger" @click="deleteImage(img.id)">删除</el-button>
          </div>
          <div style="text-align: center; margin-top: 8px">
            <el-button size="small" @click="openInsert(idx)">在此处插入</el-button>
          </div>
        </el-card>

        <el-dialog v-model="insertDialogVisible[idx]" title="插入图片" width="400px">
          <el-upload
              :auto-upload="false"
              :multiple="true"
              :show-file-list="false"
              :on-change="handleInsertFileChange"
              drag
              class="upload-area"
              accept="image/*"
          >
            <el-icon><upload-filled /></el-icon>
            <div class="el-upload__text">拖拽或点击上传图片</div>
          </el-upload>

          <div v-if="insertFileList.length > 0" style="margin-top: 16px">
            <h5>待插入文件:</h5>
            <el-tag
                v-for="(file, index) in insertFileList"
                :key="index"
                style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ file.name }}
            </el-tag>
          </div>

          <template #footer>
            <span class="dialog-footer">
              <el-button @click="insertDialogVisible[idx] = false">取消</el-button>
              <el-button
                  type="primary"
                  @click="uploadInsertFiles(img.id)"
                  :disabled="insertFileList.length === 0"
                  :loading="insertUploading"
              >
                {{ insertUploading ? '插入中...' : '插入上传' }}
              </el-button>
            </span>
          </template>
        </el-dialog>
      </el-col>
    </el-row>

    <el-image-viewer
        v-if="viewerVisible"
        :url-list="images.map(i => getFullImageUrl(i.imageUrl))"
        :initial-index="viewerIndex"
        @close="viewerVisible = false"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import { ElMessage } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import { useUserStore } from '../store/user';
import ElImageViewer from 'element-plus/es/components/image-viewer/index';

const route = useRoute();
const images = ref([]);
const userStore = useUserStore();
const bookId = route.params.id;

const router = useRouter();
const goBack = () => router.back();

// 文件上传相关
const fileList = ref([]);
const uploading = ref(false);
const insertFileList = ref([]);
const insertUploading = ref(false);

// 按文件名自然排序
const naturalSort = (a, b) => {
  return a.localeCompare(b, undefined, {
    numeric: true,
    sensitivity: 'base'
  });
};

// 排序后的文件列表
const sortedFileList = computed(() => {
  return [...fileList.value].sort((a, b) => naturalSort(a.name, b.name));
});

// 处理文件选择
const handleFileChange = (file, files) => {
  fileList.value = files.map(f => f.raw).filter(f => f && f.type.startsWith('image/'));
};

// 处理插入文件选择
const handleInsertFileChange = (file, files) => {
  insertFileList.value = files.map(f => f.raw).filter(f => f && f.type.startsWith('image/'));
};

// 清空文件列表
const clearFiles = () => {
  fileList.value = [];
};

// 顺序上传文件
const uploadFiles = async () => {
  if (fileList.value.length === 0) return;

  uploading.value = true;
  try {
    const sortedFiles = sortedFileList.value;

    for (let i = 0; i < sortedFiles.length; i++) {
      const file = sortedFiles[i];
      const formData = new FormData();
      formData.append('file', file);

      await api.post(`/book-images/upload/${bookId}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: `Bearer ${userStore.token}`
        }
      });
    }

    ElMessage.success(`成功上传 ${sortedFiles.length} 个文件`);
    fileList.value = [];
    await fetchImages();
  } catch (error) {
    console.error('Upload failed:', error);
    ElMessage.error('上传失败: ' + (error.response?.data || error.message));
  } finally {
    uploading.value = false;
  }
};

// 插入文件上传
const uploadInsertFiles = async (afterId) => {
  if (insertFileList.value.length === 0) return;

  insertUploading.value = true;
  try {
    const sortedFiles = [...insertFileList.value].sort((a, b) => naturalSort(a.name, b.name));

    for (let i = 0; i < sortedFiles.length; i++) {
      const file = sortedFiles[i];
      const formData = new FormData();
      formData.append('file', file);

      // 计算正确的afterId（前面的文件插入后，afterId需要更新）
      let currentAfterId = afterId;
      if (i > 0) {
        // 重新获取最新的图片列表来找到正确的afterId
        const { data } = await api.get(`/book-images/book/${bookId}`);
        const currentImages = (data || []).slice().sort((a, b) => (a.imgOrder ?? 0) - (b.imgOrder ?? 0));
        const originalAfterIndex = currentImages.findIndex(img => img.id === afterId);
        if (originalAfterIndex !== -1 && originalAfterIndex + i < currentImages.length) {
          currentAfterId = currentImages[originalAfterIndex + i].id;
        }
      }

      await api.post(`/book-images/upload/${bookId}?afterId=${currentAfterId}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: `Bearer ${userStore.token}`
        }
      });
    }

    ElMessage.success(`成功插入 ${sortedFiles.length} 个文件`);
    insertFileList.value = [];
    insertDialogVisible.value = [];
    await fetchImages();
  } catch (error) {
    console.error('Insert upload failed:', error);
    ElMessage.error('插入失败: ' + (error.response?.data || error.message));
  } finally {
    insertUploading.value = false;
  }
};

const fetchImages = async () => {
  const { data } = await api.get(`/book-images/book/${bookId}`);
  // 按imgOrder排序
  images.value = (data || []).slice().sort((a, b) => (a.imgOrder ?? 0) - (b.imgOrder ?? 0));
};

const deleteImage = async id => {
  await api.delete(`/book-images/${id}`);
  ElMessage.success('删除成功');
  fetchImages();
};

const getFullImageUrl = (url) => {
  if (url && url.startsWith('/uploads/')) {
    return `http://localhost:8080${url}`;
  }
  return url;
};

// 从URL中提取文件名
const getFileName = (url) => {
  if (!url) return '';
  const parts = url.split('/');
  const fileName = parts[parts.length - 1];
  // 移除时间戳前缀 (例如: "1234567890_image.jpg" -> "image.jpg")
  const timestampMatch = fileName.match(/^\d+_(.+)$/);
  return timestampMatch ? timestampMatch[1] : fileName;
};

const viewerVisible = ref(false);
const viewerIndex = ref(0);
const openViewer = idx => {
  viewerIndex.value = idx;
  viewerVisible.value = true;
};

const insertDialogVisible = ref([]);
const openInsert = idx => {
  insertDialogVisible.value = images.value.map((_, i) => i === idx);
  insertFileList.value = []; // 清空之前的选择
};

onMounted(fetchImages);
</script>

<style scoped>
.header-bar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.upload-area {
  width: 300px;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #f8fafc;
}

.file-preview {
  margin-bottom: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e1e5e9;
}

.img-card {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.book-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.2s;
}

.book-img:hover {
  transform: scale(1.05);
}

.image-info {
  padding: 8px 0;
  font-size: 12px;
  color: #666;
  text-align: center;
}

.image-filename {
  font-weight: bold;
  margin-bottom: 4px;
  word-break: break-all;
}

.image-order {
  color: #999;
}

.el-upload__tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>