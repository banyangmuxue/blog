const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
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
