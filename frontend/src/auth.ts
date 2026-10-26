import { UserResponse } from './data/UserResponse';

const TOKEN_KEY = 'token';
const USER_KEY = 'user';

// Store the JWT and the logged-in user for this browser tab
export const saveSession = (token: string, user: UserResponse): void => {
  sessionStorage.setItem(TOKEN_KEY, token);
  sessionStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const getToken = (): string | null => sessionStorage.getItem(TOKEN_KEY);

export const getCurrentUser = (): UserResponse | null => {
  const storedUser = sessionStorage.getItem(USER_KEY);
  if (!storedUser) {
    return null;
  }
  try {
    return JSON.parse(storedUser) as UserResponse;
  } catch {
    return null;
  }
};

export const clearSession = (): void => {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
};
