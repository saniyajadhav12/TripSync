// Import necessary React modules and hooks
import React, { useEffect, useState } from "react";
import Header from "../components/Header"; // Import the updated Header component
import "../styles/HomePage.css"; // Import the CSS for styling
import { getAllTrips } from "../api/tripApi"; // Import API function for fetching trips
import { TripDTO } from "../data/tripDTO"; // Import Trip Data Transfer Object type
import { useNavigate } from "react-router-dom"; // Import navigation hook
import Img1 from "../images/Img2.jpg"; // Import sample images
import Img2 from "../images/Img3.jpg";
import Img3 from "../images/Img1.jpg";
import Img4 from "../images/Img4.jpg";
import Img5 from "../images/Img5.jpg";
import Img6 from "../images/Img6.jpg";
import Img7 from "../images/Img2.jpg";
import Img8 from "../images/Img3.jpg";
import Img9 from "../images/Img1.jpg";
import Img10 from "../images/Img4.jpg";
import Img11 from "../images/Img1.jpg";
import Img12 from "../images/Img4.jpg";

// Define the functional component for the HomePage
const HomePage: React.FC = () => {
  // State to manage fetched trips
  const [trips, setTrips] = useState<TripDTO[]>([]);

  // State to manage dropdown visibility for user profile actions
  const [isDropdownOpen, setIsDropdownOpen] = useState<boolean>(false);

  // State to manage loading indicator
  const [loading, setLoading] = useState<boolean>(true);

  // State to manage error messages
  const [error, setError] = useState<string>("");

  // Navigation hook for programmatic route changes
  const navigate = useNavigate();

  // Fetch trips on component mount
  useEffect(() => {
    const fetchTrips = async () => {
      try {
        setLoading(true); // Start the loading indicator
        const response = await getAllTrips(); // Fetch all trips from the API
        setTrips(response); // Update the state with fetched trips
      } catch (error) {
        console.error(error);
        setError("Failed to fetch trips. Please try again later."); // Display error message
      } finally {
        setLoading(false); // Stop the loading indicator
      }
    };

    fetchTrips();
  }, []); // Empty dependency array ensures this runs only once

  // Navigate to the Create Trip page
  const handleAddTrip = () => {
    window.location.href = "/create-trip";
  };

  // Toggle the visibility of the dropdown menu for profile actions
  const handleProfileClick = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  // Navigate to the user profile page
  const navigateToProfile = () => {
    window.location.href = "/profile";
  };

  // Placeholder function for logout (to be implemented)
  const logout = () => {
    console.log("Logout");
  };

  // Array of sample images for trip thumbnails
  const tripImages = [Img1, Img2, Img3, Img4, Img5, Img6, Img7, Img8, Img9, Img10, Img11, Img12];

  // Handle click on a trip card to navigate to its details page
  const handleTripClick = (id: number) => {
    navigate(`/trip/${id}`);
  };

  // Component rendering
  return (
    <div className="homepage">
      {/* Hero section with title and description */}
      <div className="hero-section">
        <h1>Your Adventures, Your Stories</h1>
        <p>Explore, Share, and Relive Your Trips</p>
      </div>

      {/* Main container for the page */}
      <div className="home-page-container">
        {/* Header component with user actions */}
        <Header
          isAddPost={true} // Show "Add Post" button
          isUserProfile={true} // Show user profile actions
          addPost={handleAddTrip} // Function for adding a new trip
          handleProfileClick={handleProfileClick} // Function to toggle profile dropdown
          isDropdownOpen={isDropdownOpen} // State for dropdown visibility
          navigateToProfile={navigateToProfile} // Function to navigate to profile
          logout={logout} // Function for logging out
        />

        {/* Show error message if fetching trips fails */}
        {error ? (
          <p className="error">{error}</p>
        ) : trips.length > 0 ? (
          /* Render list of trips if available */
          <div className="trip-list">
            {trips.map((trip, index) => (
              <div
                className="trip-card-container"
                key={trip.id}
                onClick={() => handleTripClick(trip.id)} // Navigate to trip details on click
              >
                {/* Display trip image */}
                <div className="trip-image">
                  <img src={tripImages[index % tripImages.length]} alt="Trip" />
                </div>
                {/* Display trip details */}
                <div className="trip-content">
                  <h3>{trip.destination}</h3>
                  <p>
                    <strong>Description:</strong> {trip.description}
                  </p>
                  <p>
                    <strong>Start Date:</strong> {trip.startDate}
                  </p>
                  <p>
                    <strong>End Date:</strong> {trip.endDate}
                  </p>
                  <button className="trip-button">View Details</button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          /* Show a placeholder if no trips are available */
          <div className="no-trips-container">
            <img
              src="https://pumpernickelpixie.com/wp-content/uploads/2015/06/31.gif"
              alt="No trips animation"
              className="no-trips-gif"
            />
            <p>No trips found. Click "+" to add a new trip!</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default HomePage;
