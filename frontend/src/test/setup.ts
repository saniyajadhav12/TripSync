import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// Unmount rendered components and forget any session between tests
afterEach(() => {
  cleanup();
  sessionStorage.clear();
});
