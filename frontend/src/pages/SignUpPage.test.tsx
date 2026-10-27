import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import SignUpPage from './SignUpPage';
import { createUser } from '../api/userApi';

// The API module is mocked so no request is ever sent
vi.mock('../api/userApi', () => ({
  createUser: vi.fn(),
}));

const fillForm = (container: HTMLElement, values: Record<string, string>) => {
  for (const [name, value] of Object.entries(values)) {
    const input = container.querySelector<HTMLInputElement>(`input[name="${name}"]`);
    if (!input) throw new Error(`No input named ${name}`);
    fireEvent.change(input, { target: { value } });
  }
};

const validForm = {
  firstName: 'Ada',
  lastName: 'Lovelace',
  email: 'ada@example.com',
  username: 'ada',
  password: 'correct-horse',
  confirmPassword: 'correct-horse',
};

const renderPage = () =>
  render(
    <MemoryRouter>
      <SignUpPage />
    </MemoryRouter>
  );

describe('SignUpPage client-side checks', () => {
  beforeEach(() => {
    vi.mocked(createUser).mockReset();
  });

  it('rejects passwords that do not match', () => {
    const { container } = renderPage();
    fillForm(container, { ...validForm, confirmPassword: 'something-else' });

    fireEvent.click(screen.getByRole('button', { name: 'Sign Up' }));

    expect(screen.getByText("Passwords don't match!")).toBeInTheDocument();
    expect(createUser).not.toHaveBeenCalled();
  });

  it('rejects a password shorter than 8 characters', () => {
    const { container } = renderPage();
    fillForm(container, { ...validForm, password: 'short', confirmPassword: 'short' });

    fireEvent.click(screen.getByRole('button', { name: 'Sign Up' }));

    expect(screen.getByText('Password must be at least 8 characters.')).toBeInTheDocument();
    expect(createUser).not.toHaveBeenCalled();
  });

  it('submits a valid form', () => {
    vi.mocked(createUser).mockReturnValue(new Promise(() => {})); // stays pending; only the call matters
    const { container } = renderPage();
    fillForm(container, validForm);

    fireEvent.click(screen.getByRole('button', { name: 'Sign Up' }));

    expect(createUser).toHaveBeenCalledWith(expect.objectContaining({
      firstName: 'Ada',
      email: 'ada@example.com',
      username: 'ada',
      password: 'correct-horse',
    }));
  });
});
