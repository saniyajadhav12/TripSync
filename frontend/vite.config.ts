import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Your Spring Boot API URL
        changeOrigin: true,  // Change the origin of the host header to the target URL
        secure: false,       // Disable SSL verification (set to true if using HTTPS)
        rewrite: (path) => path.replace(/^\/api/, '')  // Rewrite the /api part
      }
    }
  }
});