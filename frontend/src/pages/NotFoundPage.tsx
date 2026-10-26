import React from "react";
import { Link } from "react-router-dom";
import { getToken } from "../auth";

const NotFoundPage: React.FC = () => {
  const isLoggedIn = Boolean(getToken());

  return (
    <div style={{ textAlign: "center", padding: "4rem 1rem" }}>
      <h1>Page not found</h1>
      <p>The page you are looking for does not exist.</p>
      <Link className="btn btn-primary" to={isLoggedIn ? "/home" : "/login"}>
        {isLoggedIn ? "Go to your trips" : "Go to login"}
      </Link>
    </div>
  );
};

export default NotFoundPage;
