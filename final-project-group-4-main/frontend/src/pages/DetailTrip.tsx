import React, { useEffect, useState } from 'react';
import '../styles/DetailTrip.css';
import { TripDTO } from '../data/tripDTO';
import { ActivityDTO } from '../data/ActivityDTO';
import { AccommodationDTO } from '../data/AccommodationDTO';
import { getTripById } from '../api/tripApi';
import { useParams } from 'react-router-dom';
import { getActivitiesByTripId } from '../api/activity';
import { getAccommodationsByTripId } from '../api/accommodation';
import Header from '../components/Header'

import {
    MapPin,
    Calendar,
    Bed,
    Info,
    Activity,
    Mountain,
    Plane,
    Compass
} from 'lucide-react';

const DetailTrip: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [trip, setTrip] = useState<TripDTO | null>(null);
    const [accommodations, setAccommodations] = useState<AccommodationDTO[]>([]);
    const [activities, setActivities] = useState<ActivityDTO[]>([]);
    const [selectedTab, setSelectedTab] = useState<string>('overview');
    const [isDropdownOpen, setIsDropdownOpen] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        console.log("trip id: " + id);
      
        const fetchData = async () => {
          try {
            setLoading(true);
      
            if (!id) {
              throw new Error("Trip ID is missing");
            }
      
            // Fetch trip details
            const tripResponse = await getTripById(parseInt(id));
            console.log("Trip fetched (DetailTrip): ", tripResponse);
            setTrip(tripResponse);
      
            // Fetch accommodations by trip ID
            const accommodationsResponse = await getAccommodationsByTripId(parseInt(id));
            console.log("Accommodations fetched: ", accommodationsResponse);
            setAccommodations(accommodationsResponse);
      
            // Fetch activities by trip ID
            const activitiesResponse = await getActivitiesByTripId(parseInt(id));
            console.log("Activities fetched: ", activitiesResponse);
              setActivities(activitiesResponse);
      
          } catch (error) {
            console.error(error);
            setError("Failed to fetch data. Please try again later.");
          } finally {
            setLoading(false);
          }
        };
      
        fetchData();
      }, [id]);
      
    // Navigate to the user profile page
    const navigateToProfile = () => {
        window.location.href = "/profile";
    };


    const logout = () => {
        alert('Logging out...');
    };

    const handleProfileClick = () => {
        setIsDropdownOpen((prev) => !prev);
    };

    // Sample data (you would replace this with actual data)
    const tripData: TripDTO = {
        id: 0,
        destination: trip?.destination,
        description: trip?.description,
        startDate: trip?.startDate,
        endDate: trip?.endDate,
        userId: trip?.userId
    }

    // Render methods remain the same as in the previous implementation
    const renderOverview = () => (
        <div className="activity-section overview-section">
            <div className="activity-detail">
                <MapPin className="icon location-icon" />
                <p><strong>Destination:</strong> {tripData.destination}</p>
            </div>
            <div className="activity-detail">
                <Calendar className="icon calendar-icon" />
                <p><strong>Start Date:</strong> {tripData.startDate}</p>
            </div>
            <div className="activity-detail">
                <Calendar className="icon calendar-icon" />
                <p><strong>End Date:</strong> {tripData.endDate}</p>
            </div>
        </div>
    );

    const renderDescription = () => (
        <div className="activity-section description-section">
            <div className="section-header">
                <Info className="icon description-icon" />
            </div>
            <p className="description-text">{tripData.description}</p>
        </div>
    );

    const renderActivity = () => (
        <div className="activity-section activity-details-section">
            <div className="section-header">
                <Activity className="icon activity-icon" />
            </div>
            {/* Loop through the activities and display each one */}
            {activities.map((activity) => (
                <div key={activity.id} className="activity-item">
                    <h4>{activity.title}</h4>
                    <p>{activity.description}</p>
                    <ul>
                        <li><strong>Category:</strong> {activity.category}</li>
                        <li><strong>Start Date:</strong> {activity.startDateTime}</li>
                        <li><strong>End Date:</strong> {activity.endDateTime}</li>
                        <li><strong>Cost:</strong> {activity.cost}</li>
                    </ul>
                </div>
            ))}
        </div>
    );
    

    const renderAccommodation = () => (
        <div className="activity-section accommodation-details-section">
            <div className="section-header">
                <Bed className="icon accommodation-icon" />
            </div>
            {/* Loop through the accommodations and display each one */}
            {accommodations.map((accommodation) => (
                <div key={accommodation.id} className="accommodation-item">
                    <h4>{accommodation.name}</h4>
                    <ul>
                        <li><strong>Type:</strong> {accommodation.type}</li>
                        <li><strong>Address:</strong> {accommodation.address}</li>
                        <li><strong>Check-in Date:</strong> {accommodation.checkInDate}</li>
                        <li><strong>Check-out Date:</strong> {accommodation.checkOutDate}</li>
                        <li><strong>Cost:</strong> {accommodation.cost}</li>
                    </ul>
                </div>
            ))}
        </div>
    );        

    return (
        <div className="activity-accommodation-container">
            <Header
                isAddPost={false}
                isUserProfile={true}
                handleProfileClick={handleProfileClick}
                logout={logout}
                isDropdownOpen={isDropdownOpen}
                navigateToProfile={navigateToProfile} // Function to navigate to profile
                bgColor="#fcefd2"
                textColor="#000000"
            />
            
            {/* Travel-themed SVG background elements */}
            <div className="travel-background">
                <Mountain className="bg-mountain mountain-left" />
                <Plane className="bg-plane" />
                <Compass className="bg-compass" />
            </div>

            <div className="content-wrapper">
                <h1 className="page-title">{tripData.destination}</h1>

                <div className="tab-navigation">
                    <button
                        className={`tab-btn ${selectedTab === 'overview' ? 'active' : ''}`}
                        onClick={() => setSelectedTab('overview')}
                    >
                        Overview
                    </button>
                    <button
                        className={`tab-btn ${selectedTab === 'description' ? 'active' : ''}`}
                        onClick={() => setSelectedTab('description')}
                    >
                        Description
                    </button>
                    <button
                        className={`tab-btn ${selectedTab === 'activity' ? 'active' : ''}`}
                        onClick={() => setSelectedTab('activity')}
                    >
                        Activity
                    </button>
                    <button
                        className={`tab-btn ${selectedTab === 'accommodation' ? 'active' : ''}`}
                        onClick={() => setSelectedTab('accommodation')}
                    >
                        Accommodation
                    </button>
                </div>

                <div className="tab-content">
                    {selectedTab === 'overview' && renderOverview()}
                    {selectedTab === 'description' && renderDescription()}
                    {selectedTab === 'activity' && renderActivity()}
                    {selectedTab === 'accommodation' && renderAccommodation()}
                </div>
            </div>
        </div>
    );
};

export default DetailTrip;