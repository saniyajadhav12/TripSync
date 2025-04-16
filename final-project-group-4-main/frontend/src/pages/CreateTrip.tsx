import React, { useState } from "react";
import Header from "../components/Header";
import { createTrip } from "../api/tripApi";
import "../styles/CreateTrip.css";

const CreateTrip: React.FC = () => {
  const [trip, setTrip] = useState({
    description: "",
    destination: "",
    startDate: "",
    endDate: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const storedUser = sessionStorage.getItem("user");
  const user = storedUser ? JSON.parse(storedUser) : null;

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setTrip({ ...trip, [name]: value });
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user?.id) {
      setError("User is not authenticated.");
      return;
    }

    if (new Date(trip.startDate) > new Date(trip.endDate)) {
      setError("Start date must be before the end date.");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const tripData = {
        ...trip,
        id: 0,
        userId: Number(user.id),
      };

      const createdTrip = await createTrip(tripData);
      window.location.href = `/activity/${createdTrip.id}`;
    } catch (error) {
      console.error("Error creating trip:", error);
      setError("Error creating trip. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    window.history.back();
  };

  return (
    <div className="create-trip-page">
      <Header
        isAddPost={false}
        isUserProfile={true}
        bgColor="#fcefd2"
        textColor="#000000"
      />
      <div className="trip-form-container">
        <div className="form-wrapper">
          <h2>New Trip</h2>
          {error && <p className="trip-error">{error}</p>}
          <form onSubmit={handleCreate}>
            <label htmlFor="destination">Destination:</label>
            <input
              type="text"
              id="destination"
              name="destination"
              value={trip.destination}
              onChange={handleInputChange}
              required
            />
            <label htmlFor="description">Description:</label>
            <input
              type="text"
              id="description"
              name="description"
              value={trip.description}
              onChange={handleInputChange}
              required
            />
            <label htmlFor="startDate">Start Date:</label>
            <input
              type="date"
              id="startDate"
              name="startDate"
              value={trip.startDate}
              onChange={handleInputChange}
              required
            />
            <label htmlFor="endDate">End Date:</label>
            <input
              type="date"
              id="endDate"
              name="endDate"
              value={trip.endDate}
              onChange={handleInputChange}
              required
            />
            <div className="trip-form-buttons">
              <button
                type="button"
                className="trip-cancel-button"
                onClick={handleCancel}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="trip-create-button"
                disabled={loading}
              >
                {loading ? "Creating..." : "Create"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateTrip;
