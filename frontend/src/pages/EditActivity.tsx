import React, { useState, useEffect } from 'react';
import { Hotel, Plane } from 'lucide-react';
import Header from '../components/Header'; // Import Header component
import '../styles/ActivityAccommodation.css';
import axios from 'axios';

type FormType = 'activity' | 'accommodation';

const EditActivity: React.FC = () => {
  const [activeForm, setActiveForm] = useState<FormType | null>(null);
  const [formData, setFormData] = useState<any>({
    id: '',
    title: '',
    cost: 0,
    category: '',
    description: '',
    startDateTime: '',
    endDateTime: '',
    address: '',
    type: '',
    checkInDate: '',
    checkOutDate: '',
    review: '',
  });
  const [activities, setActivities] = useState<any[]>([]);
  const [accommodations, setAccommodations] = useState<any[]>([]);
  const [tripId] = useState<number>(1); // Replace with the actual trip ID
  const [userId] = useState<number>(1); // Replace with the logged-in user's ID

  const API_BASE_URL = 'http://localhost:8080/api';

  // Fetch activities and accommodations for the specific user and trip
  const fetchData = async () => {
    try {
      const activitiesResponse = await axios.get(
        `${API_BASE_URL}/activities/trip/${tripId}?userId=${userId}`
      );
      setActivities(activitiesResponse.data);

      const accommodationsResponse = await axios.get(
        `${API_BASE_URL}/accommodations/trip/${tripId}?userId=${userId}`
      );
      setAccommodations(accommodationsResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
      alert('Failed to fetch data. Please try again.');
    }
  };

  useEffect(() => {
    fetchData();
  }, [tripId]);

  const handleEditClick = (item: any, type: FormType) => {
    setFormData(item); // Pre-fill the form with the selected item's details
    setActiveForm(type);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const payload = { ...formData, tripId, userId };

      if (activeForm === 'activity') {
        await axios.put(`${API_BASE_URL}/activities/${formData.id}`, payload);
        alert('Activity updated successfully!');
      } else if (activeForm === 'accommodation') {
        await axios.put(
          `${API_BASE_URL}/accommodations/${formData.id}`,
          payload
        );
        alert('Accommodation updated successfully!');
      }

      setFormData({
        id: '',
        title: '',
        cost: 0,
        category: '',
        description: '',
        startDateTime: '',
        endDateTime: '',
        address: '',
        type: '',
        checkInDate: '',
        checkOutDate: '',
        review: '',
      });
      setActiveForm(null);
      fetchData();
    } catch (error) {
      console.error('Error updating data:', error);
      alert('Failed to update data. Please try again.');
    }
  };

  const handleCancel = () => {
    setFormData({
      id: '',
      title: '',
      cost: 0,
      category: '',
      description: '',
      startDateTime: '',
      endDateTime: '',
      address: '',
      type: '',
      checkInDate: '',
      checkOutDate: '',
      review: '',
    });
    setActiveForm(null);
  };

  const handleInputChange = (field: string, value: string | number) => {
    setFormData({ ...formData, [field]: value });
  };

  const navigateToProfile = () => {
    window.location.href = '/profile';
  };

  const logout = () => {
    sessionStorage.removeItem('user');
    window.location.href = '/';
  };

  return (
    <div className="new-image">
      <Header
        isAddPost={false}
        isUserProfile={true}
        navigateToProfile={navigateToProfile}
        logout={logout}
      />
      <div className="activity-accommodation-unique-container">
        <h2>Edit Activities and Accommodations</h2>
        <main>
          <div className="edit-lists">
            <h3>Activities</h3>
            <ul>
              {activities.map((activity) => (
                <li key={activity.id}>
                  <span>{activity.title}</span>
                  <button
                    className="edit-button"
                    onClick={() => handleEditClick(activity, 'activity')}
                  >
                    Edit
                  </button>
                </li>
              ))}
            </ul>

            <h3>Accommodations</h3>
            <ul>
              {accommodations.map((accommodation) => (
                <li key={accommodation.id}>
                  <span>{accommodation.name}</span>
                  <button
                    className="edit-button"
                    onClick={() =>
                      handleEditClick(accommodation, 'accommodation')
                    }
                  >
                    Edit
                  </button>
                </li>
              ))}
            </ul>
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
                    onChange={(e) =>
                      handleInputChange('cost', parseFloat(e.target.value))
                    }
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
                        onChange={(e) =>
                          handleInputChange('category', e.target.value)
                        }
                        required
                      >
                        <option value="">Select Category</option>
                        <option value="Adventure">Adventure</option>
                        <option value="Sightseeing">Sightseeing</option>
                        <option value="Dining">Dining</option>
                        <option value="Shopping">Shopping</option>
                        <option value="Leisure">Leisure</option>
                        <option value="Entertainment">Entertainment</option>
                        <option value="Cultural_Experience">
                          Cultural Experience
                        </option>
                        <option value="Other">Other</option>
                      </select>
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="description">Description</label>
                      <textarea
                        id="description"
                        value={formData.description}
                        onChange={(e) =>
                          handleInputChange('description', e.target.value)
                        }
                        required
                      ></textarea>
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="startDateTime">Start Date</label>
                      <input
                        type="date"
                        id="startDateTime"
                        value={formData.startDateTime}
                        onChange={(e) =>
                          handleInputChange('startDateTime', e.target.value)
                        }
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="endDateTime">End Date</label>
                      <input
                        type="date"
                        id="endDateTime"
                        value={formData.endDateTime}
                        onChange={(e) =>
                          handleInputChange('endDateTime', e.target.value)
                        }
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
                        onChange={(e) =>
                          handleInputChange('address', e.target.value)
                        }
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="type">Type</label>
                      <select
                        id="type"
                        value={formData.type}
                        onChange={(e) =>
                          handleInputChange('type', e.target.value)
                        }
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
                        onChange={(e) =>
                          handleInputChange('checkInDate', e.target.value)
                        }
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="checkOutDate">Check-Out Date</label>
                      <input
                        type="date"
                        id="checkOutDate"
                        value={formData.checkOutDate}
                        onChange={(e) =>
                          handleInputChange('checkOutDate', e.target.value)
                        }
                        required
                      />
                    </div>
                    <div className="form-group-unique">
                      <label htmlFor="review">Review (Optional)</label>
                      <textarea
                        id="review"
                        value={formData.review}
                        onChange={(e) =>
                          handleInputChange('review', e.target.value)
                        }
                      ></textarea>
                    </div>
                  </>
                )}

                <div className="form-actions-unique">
                  <button
                    type="button"
                    className="cancel-button-unique"
                    onClick={handleCancel}
                  >
                    Cancel
                  </button>
                  <button type="submit" className="submit-button-unique">
                    Edit Changes
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

export default EditActivity;
