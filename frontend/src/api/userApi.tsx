import axios from 'axios';
import axiosInstance from '../config/axios';
import { getErrorMessage } from './errors';
import { saveSession } from '../auth';
import { LoginResponse } from '../data/LoginResponse';
import { UserResponse } from '../data/UserResponse';

const API_URL = '/api/users';

// Fields the backend expects when registering a user
export interface SignupData {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string;
}

// Create a new user
export const createUser = async (userData: SignupData) => {
  try {
    return await axiosInstance.post<UserResponse>(`${API_URL}/signup`, {
      firstName: userData.firstName,
      lastName: userData.lastName,
      email: userData.email,
      username: userData.username,
      password: userData.password,
    });
  } catch (error) {
    throw new Error(getErrorMessage(error, 'Error registering user. Please try again.'));
  }
};

// Authenticate user (login) and store the token and user for later requests.
// Returns null when the username or password is wrong.
export const authenticateUser = async (username: string, password: string): Promise<UserResponse | null> => {
  try {
    const response = await axiosInstance.post<LoginResponse>(`${API_URL}/login`, { username, password });
    saveSession(response.data.token, response.data.user);
    return response.data.user;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      return null;
    }
    throw error;
  }
};
