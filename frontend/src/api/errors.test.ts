import { AxiosError, AxiosHeaders, AxiosResponse } from 'axios';
import { describe, expect, it } from 'vitest';
import { getErrorMessage } from './errors';

// Build the kind of error axios throws for a non-2xx response
const httpError = (status: number, data: unknown): AxiosError => {
  const config = { headers: new AxiosHeaders() };
  const response: AxiosResponse = { data, status, statusText: '', headers: {}, config };
  return new AxiosError('Request failed', 'ERR_BAD_REQUEST', config, undefined, response);
};

describe('getErrorMessage', () => {
  it("returns the backend's message", () => {
    const error = httpError(409, { message: 'Username is already taken' });

    expect(getErrorMessage(error, 'fallback')).toBe('Username is already taken');
  });

  it('returns the fallback when the response has no message', () => {
    expect(getErrorMessage(httpError(500, {}), 'Something went wrong')).toBe('Something went wrong');
  });

  it('returns the fallback for network errors and non-axios errors', () => {
    const networkError = new AxiosError('Network Error', 'ERR_NETWORK');

    expect(getErrorMessage(networkError, 'Offline')).toBe('Offline');
    expect(getErrorMessage(new Error('boom'), 'Offline')).toBe('Offline');
  });
});
