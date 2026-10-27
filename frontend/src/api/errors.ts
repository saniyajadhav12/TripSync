import axios from 'axios';

// Returns the backend's JSON { "message": "..." } when there is one (e.g. validation or duplicate
// errors), otherwise the given fallback text.
export const getErrorMessage = (error: unknown, fallback: string): string => {
  if (axios.isAxiosError<{ message?: string }>(error)) {
    return error.response?.data?.message || fallback;
  }
  return fallback;
};
