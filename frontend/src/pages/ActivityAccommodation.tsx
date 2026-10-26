import React, { useState, useEffect } from 'react';
import { useParams } from "react-router-dom";
import { PlusCircle, Hotel, X, Plane, Compass } from 'lucide-react';
import Header from '../components/Header'; // Import Header component
import '../styles/ActivityAccommodation.css';
import axios from 'axios';
import { Link } from 'react-router-dom';

type FormType = "activity" | "accommodation";

const ActivityAccommodation: React.FC = () => {
  const { tripId } = useParams<{ tripId: string }>();
  const [activeForm, setActiveForm] = useState<FormType | null>(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState<boolean>(false);
  const [formData, setFormData] = useState<any>({
    title: "",
    cost: 0,
    category: "",
    description: "",
    startDateTime: "",
    endDateTime: "",
    address: "",
    type: "",
    checkInDate: "",
    checkOutDate: "",
    review: "",
  });

  // const API_BASE_URL = 'http://localhost:8080/api';

  useEffect(() => {
    if (!tripId) {
      alert("Trip ID is missing. Please navigate to a valid trip.");
    }
  }, [tripId]);

  const handleInputChange = (field: string, value: string | number) => {
    setFormData({ ...formData, [field]: value });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!tripId) {
      alert("Trip ID is missing. Cannot submit.");
      return;
    }

    try {
      if (activeForm === "activity") {
        const activityPayload = {
          title: formData.title,
          cost: formData.cost,
          category: formData.category,
          description: formData.description,
          startDateTime: formData.startDateTime,
          endDateTime: formData.endDateTime,
          tripId: parseInt(tripId),
        };

        await axios.post("http://localhost:8080/api/activities", activityPayload);
        alert("Activity added successfully!");
      } else if (activeForm === "accommodation") {
        const accommodationPayload = {
          name: formData.title,
          cost: formData.cost,
          address: formData.address,
          type: formData.type,
          checkInDate: formData.checkInDate,
          checkOutDate: formData.checkOutDate,
          review: formData.review,
          tripId: parseInt(tripId),
        };

        await axios.post(
          "http://localhost:8080/api/accommodations",
          accommodationPayload
        );
        alert("Accommodation added successfully!");
      }
      setFormData({
        title: "",
        cost: 0,
        category: "",
        description: "",
        startDateTime: "",
        endDateTime: "",
        address: "",
        type: "",
        checkInDate: "",
        checkOutDate: "",
        review: "",
      });
      setActiveForm(null);
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("Failed to submit data.");
    }
  };

  const handleCancel = () => {
    setFormData({
      title: "",
      cost: 0,
      category: "",
      description: "",
      startDateTime: "",
      endDateTime: "",
      address: "",
      type: "",
      checkInDate: "",
      checkOutDate: "",
      review: "",
    });
    setActiveForm(null);
  };

  const logout = () => {
    alert('Logging out...');
  };

  const handleProfileClick = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  const navigateToProfile = () => {
    window.location.href = "/profile";
  };

  return (
    <div className="new-image">
      <Header
        isAddPost={false}
        isUserProfile={true}
        handleProfileClick={handleProfileClick}
        isDropdownOpen={isDropdownOpen}
        navigateToProfile={navigateToProfile}
        logout={logout}
      />
      <div className="activity-accommodation-unique-container">
        <div className="header-title-with-icon">
          <Compass className="title-icon" size={32} />
          <h1>Travel Planner</h1>
        </div>
        <main>
          <div className="toggle-container-unique">
            <button
              className={`toggle-button-unique ${activeForm === 'activity' ? 'active' : ''}`}
              onClick={() => setActiveForm('activity')}
            >
              <Plane className="button-icon travel-plane" size={18} />
              Add Activity
            </button>
            <button
              className={`toggle-button-unique ${activeForm === 'accommodation' ? 'active' : ''}`}
              onClick={() => setActiveForm('accommodation')}
            >
              <Hotel size={18} className="button-icon" />
              Add Accommodation
            </button>
          </div>

          {activeForm && (
            <div className="form-container-unique">
              <form onSubmit={handleSubmit} className="form-unique">
                <div className="form-header">
                  {activeForm === 'activity' ? (
                    <Plane className="form-header-icon" size={24} />
                  ) : (
                    <Hotel className="form-header-icon" size={24} />
                  )}
                </div>

                {/* Common Fields */}
                <div className="form-group-unique">
                  <label htmlFor="title">Title</label>
                  <input
                    type="text"
                    id="title"
                    value={formData.title}
                    onChange={(e) => handleInputChange('title', e.target.value)}
                    required
                  />
                </div>
                <div className="form-group-unique">
                  <label htmlFor="cost">Cost</label>
                  <input
                    type="number"
                    id="cost"
                    value={formData.cost}
                    onChange={(e) => handleInputChange('cost', parseFloat(e.target.value))}
                    required
                  />
                </div>

                {/* Activity-Specific Fields */}
                {activeForm === 'activity' && (
                  <>
                    <div className="form-group-unique">
                      <label htmlFor="category">Category</label>
                      <select
                        id="category"
                        value={formData.category}
                        onChange={(e) => handleInputChange('category', e.target.value)}
                        required
                      >
                        <option value="">Select Category</option>
                        <option value="Adventure">Adventure</option>
                        <option value="Sightseeing">Sightseeing</option>
                        <option value="Dining">Dining</option>
                        <option value="Shopping">Shopping</option>
                        <option value="Leisure">Leisure</option>
                        <option value="Entertainment">Entertainment</option>
                        <option value="Cultural_Experience">Cultural Experience</option>
                        <option value="Other">Other</option>
                      </select>
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="description">Description</label>
                      <textarea
                        id="description"
                        value={formData.description}
                        onChange={(e) => handleInputChange('description', e.target.value)}
                        required
                      ></textarea>
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="startDateTime">Start Date</label>
                      <input
                        type="date"
                        id="startDateTime"
                        value={formData.startDateTime}
                        onChange={(e) => handleInputChange('startDateTime', e.target.value)}
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="endDateTime">End Date</label>
                      <input
                        type="date"
                        id="endDateTime"
                        value={formData.endDateTime}
                        onChange={(e) => handleInputChange('endDateTime', e.target.value)}
                        required
                      />
                    </div>
                  </>
                )}

                {/* Accommodation-Specific Fields */}
                {activeForm === 'accommodation' && (
                  <>
                    <div className="form-group-unique">
                      <label htmlFor="address">Address</label>
                      <input
                        type="text"
                        id="address"
                        value={formData.address}
                        onChange={(e) => handleInputChange('address', e.target.value)}
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="type">Type</label>
                      <select
                        id="type"
                        value={formData.type}
                        onChange={(e) => handleInputChange('type', e.target.value)}
                        required
                      >
                        <option value="">Select Type</option>
                        <option value="Hotel">Hotel</option>
                        <option value="Resort">Resort</option>
                        <option value="Guesthouse">Guest House</option>
                        <option value="Hostel">Hostel</option>
                        <option value="Camping">Camping</option>
                        <option value="Villa">Villa</option>
                        <option value="Farm_Stay">Farm Stay</option>
                      </select>
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="checkInDate">Check-In Date</label>
                      <input
                        type="date"
                        id="checkInDate"
                        value={formData.checkInDate}
                        onChange={(e) => handleInputChange('checkInDate', e.target.value)}
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="checkOutDate">Check-Out Date</label>
                      <input
                        type="date"
                        id="checkOutDate"
                        value={formData.checkOutDate}
                        onChange={(e) => handleInputChange('checkOutDate', e.target.value)}
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="review">Review (Optional)</label>
                      <textarea
                        id="review"
                        value={formData.review}
                        onChange={(e) => handleInputChange('review', e.target.value)}
                      ></textarea>
                    </div>
                  </>
                )}

                <div className="form-actions-unique">
                  <button type="button" className="cancel-button-unique" onClick={handleCancel}>
                    Cancel
                  </button>
                  <button type="submit" className="submit-button-unique">
                    Submit
                  </button>
                </div>
              </form>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default ActivityAccommodation;