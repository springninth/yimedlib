// 1. 修改 ymed-ui/vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],

  // 构建配置 - 关键修改
  build: {
    outDir: '../src/main/resources/static',  // 输出到 Spring Boot 静态资源目录
    emptyOutDir: true,  // 清空输出目录
    assetsDir: 'assets'  // 资源文件目录
  },

  // 基础路径配置
  base: './',  // 重要：使用相对路径，避免部署时路径问题

  server: {
    port: 3000,  // 前端开发服务器端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '/api')
      },
      '/uploads': {  // 添加上传文件的代理
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})