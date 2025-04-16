import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // Spring Boot URL
});

export default axiosInstance;
