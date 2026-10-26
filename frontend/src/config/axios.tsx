import axios from "axios";
import { clearSession, getToken } from "../auth";

const LOGIN_PATH = "/api/users/login";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080", // Spring Boot URL
});

// Attach the JWT to every request while the user is logged in
axiosInstance.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// A 401 means the token is missing or expired: end the session and go to the login page.
// The login request is excluded, because a wrong password should just show an error on the form.
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error) && error.response?.status === 401 && error.config?.url !== LOGIN_PATH) {
      clearSession();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
