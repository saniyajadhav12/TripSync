import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Hotel, Plane } from 'lucide-react';
import Header from '../components/Header'; // Import Header component
import '../styles/ActivityAccommodation.css';
import { clearSession } from '../auth';
import { getActivitiesByTripId, updateActivity, deleteActivity } from '../api/activity';
import { getAccommodationsByTripId, updateAccommodation, deleteAccommodation } from '../api/accommodation';
import { getErrorMessage } from '../api/errors';
import { ActivityDTO, Category } from '../data/ActivityDTO';
import { AccommodationDTO, AccommodationType } from '../data/AccommodationDTO';

type FormType = 'activity' | 'accommodation';

// Form state shared by the activity and accommodation forms; '' means nothing selected yet
type EditFormData = Partial<Omit<ActivityDTO, 'category'>> &
  Partial<Omit<AccommodationDTO, 'type'>> & {
    category?: Category | '';
    type?: AccommodationType | '';
  };

const EMPTY_FORM: EditFormData = {
  title: '',
  name: '',
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
};

const EditActivity: React.FC = () => {
  const { tripId: tripIdParam } = useParams<{ tripId: string }>();
  const tripId = Number(tripIdParam);
  const navigate = useNavigate();
  const [activeForm, setActiveForm] = useState<FormType | null>(null);
  const [formData, setFormData] = useState<EditFormData>(EMPTY_FORM);
  const [activities, setActivities] = useState<ActivityDTO[]>([]);
  const [accommodations, setAccommodations] = useState<AccommodationDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true); // true until the first fetch finishes

  // Fetch the activities and accommodations of this trip
  const fetchData = useCallback(async () => {
    try {
      setActivities(await getActivitiesByTripId(tripId));
      setAccommodations(await getAccommodationsByTripId(tripId));
    } catch (error) {
      alert(getErrorMessage(error, 'Failed to fetch data. Please try again.'));
    } finally {
      setLoading(false);
    }
  }, [tripId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Pre-fill the form with the selected item's details (null values become empty fields)
  const handleEditActivity = (activity: ActivityDTO) => {
    setFormData({
      ...EMPTY_FORM,
      id: activity.id,
      title: activity.title,
      category: activity.category,
      description: activity.description ?? '',
      startDateTime: activity.startDateTime,
      endDateTime: activity.endDateTime ?? '',
      cost: activity.cost,
    });
    setActiveForm('activity');
  };

  const handleEditAccommodation = (accommodation: AccommodationDTO) => {
    setFormData({
      ...EMPTY_FORM,
      id: accommodation.id,
      name: accommodation.name,
      address: accommodation.address ?? '',
      type: accommodation.type,
      checkInDate: accommodation.checkInDate,
      checkOutDate: accommodation.checkOutDate,
      review: accommodation.review ?? '',
      cost: accommodation.cost ?? 0,
    });
    setActiveForm('accommodation');
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (formData.id === undefined) {
      return;
    }

    try {
      if (activeForm === 'activity') {
        await updateActivity(formData.id, {
          title: formData.title ?? '',
          category: formData.category as Category, // the select is required, so a category is chosen
          description: formData.description ?? '',
          startDateTime: formData.startDateTime ?? '',
          endDateTime: formData.endDateTime ?? '',
          cost: formData.cost ?? 0,
          tripId,
        });
        alert('Activity updated successfully!');
      } else if (activeForm === 'accommodation') {
        await updateAccommodation(formData.id, {
          name: formData.name ?? '',
          address: formData.address ?? '',
          type: formData.type as AccommodationType, // the select is required, so a type is chosen
          checkInDate: formData.checkInDate ?? '',
          checkOutDate: formData.checkOutDate ?? '',
          review: formData.review,
          cost: formData.cost ?? 0,
          tripId,
        });
        alert('Accommodation updated successfully!');
      }

      setFormData(EMPTY_FORM);
      setActiveForm(null);
      fetchData();
    } catch (error) {
      alert(getErrorMessage(error, 'Failed to update data. Please try again.'));
    }
  };

  const handleDelete = async (type: FormType, id: number | undefined, label: string) => {
    if (id === undefined || !window.confirm(`Delete "${label}"? This cannot be undone.`)) {
      return;
    }
    try {
      if (type === 'activity') {
        await deleteActivity(id);
      } else {
        await deleteAccommodation(id);
      }
      // Close the form if the deleted item was being edited
      if (formData.id === id && activeForm === type) {
        setFormData(EMPTY_FORM);
        setActiveForm(null);
      }
      fetchData();
    } catch (error) {
      alert(getErrorMessage(error, 'Failed to delete. Please try again.'));
    }
  };

  const handleCancel = () => {
    setFormData(EMPTY_FORM);
    setActiveForm(null);
  };

  const handleInputChange = (field: string, value: string | number) => {
    setFormData({ ...formData, [field]: value });
  };

  const navigateToProfile = () => {
    window.location.href = '/profile';
  };

  const logout = () => {
    clearSession();
    window.location.href = '/';
  };

  // Activities have a title, accommodations a name
  const nameField = activeForm === 'accommodation' ? 'name' : 'title';

  // "Cultural_Experience" -> "Cultural Experience"
  const label = (value: string) => value.replace(/_/g, ' ');

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
          <div className="page-actions">
            <button className="btn btn-secondary" onClick={() => navigate(`/trip/${tripId}`)}>
              Done – view trip
            </button>
          </div>

          {loading ? (
            <p className="loading-message">Loading...</p>
          ) : (
          <div className="edit-lists">
            <h3>Activities</h3>
            {activities.length === 0 ? (
              <div className="empty-state">
                <p>No activities yet.</p>
                <button className="btn btn-primary btn-sm" onClick={() => navigate(`/activity/${tripId}`)}>
                  Add an activity
                </button>
              </div>
            ) : (
              <ul className="item-list">
                {activities.map((activity) => (
                  <li key={activity.id} className="item-row">
                    <div className="item-main">
                      <span className="item-title">{activity.title}</span>
                      <span className="item-meta">
                        {label(activity.category)} · {activity.startDateTime} · Cost: {activity.cost}
                      </span>
                    </div>
                    <div className="item-actions">
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => handleEditActivity(activity)}
                      >
                        Edit
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete('activity', activity.id, activity.title)}
                      >
                        Delete
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}

            <h3>Accommodations</h3>
            {accommodations.length === 0 ? (
              <div className="empty-state">
                <p>No accommodations yet.</p>
                <button className="btn btn-primary btn-sm" onClick={() => navigate(`/activity/${tripId}`)}>
                  Add an accommodation
                </button>
              </div>
            ) : (
              <ul className="item-list">
                {accommodations.map((accommodation) => (
                  <li key={accommodation.id} className="item-row">
                    <div className="item-main">
                      <span className="item-title">{accommodation.name}</span>
                      <span className="item-meta">
                        {label(accommodation.type)} · {accommodation.checkInDate} → {accommodation.checkOutDate}
                        {accommodation.cost != null && ` · Cost: ${accommodation.cost}`}
                      </span>
                    </div>
                    <div className="item-actions">
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => handleEditAccommodation(accommodation)}
                      >
                        Edit
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete('accommodation', accommodation.id, accommodation.name)}
                      >
                        Delete
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
          )}

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
                  <label htmlFor={nameField}>{activeForm === 'accommodation' ? 'Name' : 'Title'}</label>
                  <input
                    type="text"
                    id={nameField}
                    value={formData[nameField]}
                    onChange={(e) => handleInputChange(nameField, e.target.value)}
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
