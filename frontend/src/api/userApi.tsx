import axios, { AxiosError } from 'axios';

// Define the base URL of your Spring Boot backend
const API_URL = 'http://localhost:8080/api/users';

// Interface for the User type
export interface User {
  id: number;
  username: string;
  email: string;
  password: string;
}

// Get all users
export const getAllUsers = async (): Promise<User[]> => {
  try {
    const response = await axios.get(API_URL);
    return response.data;  // Return the list of users
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error;
  }
};

// Get a user by ID
export const getUserById = async (id: number): Promise<User | null> => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;  // Return the user object
  } catch (error) {
    console.error(`Error fetching user with ID ${id}:`, error);
    return null;
  }
};

// Create a new user
export const createUser = async (userData: { username: string; email: string; password: string }) => {
  try {
    const response = await axios.post(`${API_URL}/signup`, userData);  // Backend API URL
    console.log(response);
    return response;
  } catch (error) {
    // Check if the error is an Axios error and capture the message
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError;
      if (axiosError.response) {
        // Log full response for debugging
        console.error("Error response:", axiosError.response);
        // Return the specific error message from the backend
        throw new Error(axiosError.response.statusText || 'Error registering user');
      } else {
        console.error('Error: ', axiosError.message);
        throw new Error(axiosError.message || 'Error registering user');
      }
    } else {
      console.error('Unexpected error: ', error);
      throw new Error('An unexpected error occurred.');
    }
  }
};

// Update an existing user
export const updateUser = async (id: number, user: User): Promise<User | null> => {
  try {
    const response = await axios.put(`${API_URL}/${id}`, user);
    return response.data;  // Return the updated user
  } catch (error) {
    console.error(`Error updating user with ID ${id}:`, error);
    return null;
  }
};

// Delete a user by ID
export const deleteUser = async (id: number): Promise<void> => {
  try {
    await axios.delete(`${API_URL}/${id}`);
    console.log(`User with ID ${id} deleted successfully`);
  } catch (error) {
    console.error(`Error deleting user with ID ${id}:`, error);
    throw error;
  }
};

// Authenticate user (login)
export const authenticateUser = async (username: string, password: string): Promise<User | null> => {
  try {
    const response = await axios.post(`${API_URL}/login`, {
      username, password
    }, 
    {
      params: { username, password },
    }
  );

    const user = response.data;  // Return the authenticated user
    console.log(user);
    if(user) {
      sessionStorage.setItem('user', JSON.stringify(user)); //Store user object 
    }

    return user;

  } catch (error) {
    console.error("Authentication failed:", error);
    return null;
  }
};