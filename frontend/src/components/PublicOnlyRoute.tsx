import { Navigate, Outlet } from "react-router-dom";
import { getToken } from "../auth";

// Login and sign-up pages: a user who is already logged in goes straight to /home
const PublicOnlyRoute = () => (getToken() ? <Navigate to="/home" replace /> : <Outlet />);

export default PublicOnlyRoute;
