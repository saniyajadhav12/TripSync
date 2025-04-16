import React, { useState, useEffect, useRef } from "react";
import "../styles/Header.css";
import { Link } from "react-router-dom";

interface HeaderProps {
  isAddPost: boolean;
  isUserProfile: boolean;
  addPost?: () => void;
  handleProfileClick?: () => void;
  isDropdownOpen?: boolean;
  navigateToProfile?: () => void;
  logout?: () => void;
  isLogout?: boolean;
  bgColor?: string;
  textColor?: string;
}

const Header: React.FC<HeaderProps> = ({
  isAddPost,
  isUserProfile,
  addPost,
  navigateToProfile,
  logout,
  isLogout,
  bgColor,
  textColor
}) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState<boolean>(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Toggle dropdown when clicking the profile icon
  const handleProfileClick = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleOutsideClick = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false); // Close the dropdown
      }
    };

    // Add event listener for clicks
    document.addEventListener("click", handleOutsideClick);

    // Cleanup event listener on unmount
    return () => {
      document.removeEventListener("click", handleOutsideClick);
    };
  }, []);

  const handleLogout = () => {
    // Confirm before logging out
    const confirmLogout = window.confirm("Are you sure you want to log out?");
    if (confirmLogout && logout) {
      logout(); // Proceed with logout if confirmed
      sessionStorage.removeItem('user');
      window.location.href = '/';
    }
  };

  return (
    <header className="header-container" style={{backgroundColor: bgColor}}>
      {/* Left Side: Add Post Button or Placeholder */}
      {isAddPost ? (
        <button className="add-post-button" onClick={addPost} aria-label="Add Post">
          &#43;
        </button>
      ) : (
        <div className="placeholder" />
      )}

      {/* Center: Website Title */}
      <div className="header-title">
        <Link to={'/home'} style={{textDecoration: 'none'}}><h1 style={{color: textColor}}>TripSync</h1></Link>
      </div>

      {/* Right Side: Profile Icon or Placeholder */}
      {isUserProfile ? (
        <div className="profile-container" ref={dropdownRef}>
          <div className="profile-icon" onClick={handleProfileClick}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="40" height="40">
              <circle cx="12" cy="8" r="4" fill= {textColor? textColor : '#ffffff'} />
              <path
                d="M12 14c-4.42 0-8 1.79-8 4v1h16v-1c0-2.21-3.58-4-8-4z"
                fill={textColor? textColor : '#ffffff'}
              />
            </svg>
          </div>
          {isDropdownOpen && (
            <div className="dropdown-menu">
              <button onClick={navigateToProfile}>Profile Page</button>
              <button onClick={handleLogout}>Logout</button> {/* Modified logout */}
            </div>
          )}
        </div>
      ) : (
        <div className="placeholder" />
      )}

      {isLogout ? (
        <div className="logout-button" onClick={handleLogout} style={{color: textColor}}>
          <svg
            fill="#ffffff"
            height="25px"
            width="25px"
            version="1.1"
            id="Capa_1"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 471.2 471.2"
          >
            <g>
              <g>
                <path d="M227.619,444.2h-122.9c-33.4,0-60.5-27.2-60.5-60.5V87.5c0-33.4,27.2-60.5,60.5-60.5h124.9c7.5,0,13.5-6,13.5-13.5 s-6-13.5-13.5-13.5h-124.9c-48.3,0-87.5,39.3-87.5,87.5v296.2c0,48.3,39.3,87.5,87.5,87.5h122.9c7.5,0,13.5-6,13.5-13.5 S235.019,444.2,227.619,444.2z" />
                <path d="M450.019,226.1l-85.8-85.8c-5.3-5.3-13.8-5.3-19.1,0c-5.3,5.3-5.3,13.8,0,19.1l62.8,62.8h-273.9c-7.5,0-13.5,6-13.5,13.5 s6,13.5,13.5,13.5h273.9l-62.8,62.8c-5.3,5.3-5.3,13.8,0,19.1c2.6,2.6,6.1,4,9.5,4s6.9-1.3,9.5-4l85.8-85.8 C455.319,239.9,455.319,231.3,450.019,226.1z" />
              </g>
            </g>
          </svg>
        </div>
      ) : (
        <div className="placeholder" />
      )}
    </header>
  );
};

export default Header;
