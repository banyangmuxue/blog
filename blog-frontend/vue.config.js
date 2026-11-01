const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  publicPath: process.env.VUE_APP_PUBLIC_PATH || '/',
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        xfwd: true,
        onProxyReq(proxyReq) {
          proxyReq.setHeader('origin', 'http://localhost:8080')
        }
      }
    }
  }
})
