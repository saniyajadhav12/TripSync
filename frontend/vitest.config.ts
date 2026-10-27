import { defineConfig } from 'vitest/config'

// Test-only configuration; vite.config.ts is used for the dev server and production build.
export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    include: ['src/**/*.test.{ts,tsx}'],
  },
});
