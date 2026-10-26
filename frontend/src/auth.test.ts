import { describe, expect, it } from 'vitest';
import { clearSession, getCurrentUser, getToken, saveSession } from './auth';
import { UserResponse } from './data/UserResponse';

const user: UserResponse = {
  id: 7,
  firstName: 'Ada',
  lastName: 'Lovelace',
  email: 'ada@example.com',
  username: 'ada',
};

describe('auth session helpers', () => {
  it('saves the token and user and reads them back', () => {
    saveSession('test-token', user);

    expect(getToken()).toBe('test-token');
    expect(getCurrentUser()).toEqual(user);
  });

  it('clearSession removes both the token and the user', () => {
    saveSession('test-token', user);

    clearSession();

    expect(getToken()).toBeNull();
    expect(getCurrentUser()).toBeNull();
    expect(sessionStorage.length).toBe(0);
  });

  it('returns null when nothing is stored', () => {
    expect(getToken()).toBeNull();
    expect(getCurrentUser()).toBeNull();
  });

  it('returns null instead of throwing when the stored user is not valid JSON', () => {
    sessionStorage.setItem('user', '{not json');

    expect(getCurrentUser()).toBeNull();
  });
});
