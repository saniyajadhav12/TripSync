import { Navigate, Outlet } from "react-router-dom";
import { getToken } from "../auth";

// Renders the nested routes only when the user is logged in; otherwise sends them to /login
const ProtectedRoute = () => (getToken() ? <Outlet /> : <Navigate to="/login" replace />);

export default ProtectedRoute;
