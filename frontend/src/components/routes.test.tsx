import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import ProtectedRoute from './ProtectedRoute';
import PublicOnlyRoute from './PublicOnlyRoute';

// Minimal route tree mirroring App.tsx, with placeholder pages
const renderAt = (path: string) =>
  render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route element={<PublicOnlyRoute />}>
          <Route path="/login" element={<p>login page</p>} />
        </Route>
        <Route element={<ProtectedRoute />}>
          <Route path="/home" element={<p>home page</p>} />
        </Route>
      </Routes>
    </MemoryRouter>
  );

describe('ProtectedRoute', () => {
  it('redirects to /login when there is no token', () => {
    renderAt('/home');

    expect(screen.getByText('login page')).toBeInTheDocument();
    expect(screen.queryByText('home page')).not.toBeInTheDocument();
  });

  it('renders the page when a token exists', () => {
    sessionStorage.setItem('token', 'test-token');

    renderAt('/home');

    expect(screen.getByText('home page')).toBeInTheDocument();
  });
});

describe('PublicOnlyRoute', () => {
  it('shows the login page when logged out', () => {
    renderAt('/login');

    expect(screen.getByText('login page')).toBeInTheDocument();
  });

  it('redirects a logged-in user to /home', () => {
    sessionStorage.setItem('token', 'test-token');

    renderAt('/login');

    expect(screen.getByText('home page')).toBeInTheDocument();
    expect(screen.queryByText('login page')).not.toBeInTheDocument();
  });
});
