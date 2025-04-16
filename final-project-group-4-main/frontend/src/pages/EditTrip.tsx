import React, { useState, useEffect } from "react"; // React and useState, useEffect hooks
import Header from "../components/Header"; // Header component for page navigation
import { getTripById, updateTrip } from "../api/tripApi"; // API functions to get and update a trip
import "../styles/CreateTrip.css"; // CSS for styling the component
import { useParams } from "react-router-dom"; // Hook to access route parameters

const EditTrip: React.FC = () => {
  // Get the trip ID from URL parameters
  const { id } = useParams<{ id: string }>();

  // State for trip data
  const [trip, setTrip] = useState({
    description: "",
    destination: "",
    startDate: "",
    endDate: "",
  });

  // State to manage loading status during API call
  const [loading, setLoading] = useState(false);

  // State to handle errors (if any occur during trip update)
  const [error, setError] = useState<string | null>(null);

  // Retrieve the logged-in user from session storage
  const storedUser = sessionStorage.getItem("user");
  const user = storedUser ? JSON.parse(storedUser) : null;

  // Fetch the trip data by its ID when the component mounts
  useEffect(() => {
    const fetchTrip = async () => {
      if (id) {
        try {
          const fetchedTrip = await getTripById(Number(id));
          if (fetchedTrip) {
            setTrip(fetchedTrip); // Set fetched trip data to state
          } else {
            setError("Trip not found."); // Handle case if trip is not found
          }
        } catch (error) {
          console.log(error);
          setError("Error fetching trip data. Please try again.");
        }
      }
    };

    fetchTrip();
  }, [id]);

  // Handle input changes for form fields
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target; // Extract name and value from input
    setTrip({ ...trip, [name]: value }); // Update corresponding field in trip state
  };

  // Handle form submission for updating the trip
  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault(); // Prevent default form submission behavior

    // Validate user authentication
    if (!user.id) {
      setError("User is not authenticated."); // Set error if user is not logged in
      return;
    }

    // Validate date range
    if (new Date(trip.startDate) >= new Date(trip.endDate)) {
      setError("Start date must be before the end date.");
      return;
    }

    setLoading(true); // Set loading to true to show the "Updating..." button state
    setError(null); // Clear any previous errors

    try {
      // Prepare updated trip data with user ID
      const tripData = {
        ...trip,
        userId: Number(user.id), // Convert user ID to a number
      };

      const updatedTrip = await updateTrip(Number(id), tripData); // Call API to update the trip

      if (updatedTrip) {
        // Redirect to profile page upon successful update
        window.location.href = "/profile";
      } else {
        setError("Error updating trip. Please try again.");
      }
    } catch (error) {
      console.log(error); // Log error for debugging
      setError("Error updating trip. Please try again."); // Display user-friendly error message
    } finally {
      setLoading(false); // Reset loading state
    }
  };

  // Handle cancel button click, navigate back to the previous page
  const handleCancel = () => {
    window.history.back(); // Go back to the previous page in browser history
  };

  // Navigate to the user profile page
  const navigateToProfile = () => {
    window.location.href = "/profile";
  };

  // Handle user logout (currently logs message to the console)
  const logout = () => {
    console.log("Logout"); // Placeholder for logout functionality
  };

  return (
    <div className="create-trip-page">
      {/* Header component with customized props */}
      <Header
        isAddPost={false}
        isUserProfile={true}
        navigateToProfile={navigateToProfile}
        logout={logout}
        bgColor="#fcefd2" // Custom background color
        textColor="#000000" // Custom text color
      />

      {/* Form container for editing the trip */}
      <div className="trip-form-container">
        <div className="form-wrapper">
          {/* Page Title */}
          <h2>Edit Trip</h2>

          {/* Display error message if any */}
          {error && <p className="trip-error">{error}</p>}

          {/* Trip editing form */}
          <form onSubmit={handleUpdate}>
            <label htmlFor="destination">Destination:</label>
            <input
              type="text"
              id="destination"
              name="destination"
              value={trip.destination}
              onChange={handleInputChange}
              required // Field is required
            />

            <label htmlFor="description">Description:</label>
            <input
              type="text"
              id="description"
              name="description"
              value={trip.description}
              onChange={handleInputChange}
              required // Field is required
            />

            <label htmlFor="startDate">Start Date:</label>
            <input
              type="date"
              id="startDate"
              name="startDate"
              value={trip.startDate}
              onChange={handleInputChange}
              required // Field is required
            />

            <label htmlFor="endDate">End Date:</label>
            <input
              type="date"
              id="endDate"
              name="endDate"
              value={trip.endDate}
              onChange={handleInputChange}
              required // Field is required
            />

            {/* Action buttons for form */}
            <div className="trip-form-buttons">
              {/* Cancel button */}
              <button
                type="button"
                className="trip-cancel-button"
                onClick={handleCancel}
              >
                Cancel
              </button>

              {/* Submit button */}
              <button
                type="submit"
                className="trip-create-button"
                disabled={loading} // Disable button while loading
              >
                {loading ? "Updating..." : "Update"} {/* Dynamic button text */}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditTrip;