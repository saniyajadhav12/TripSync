import React, { ReactNode, useEffect, useState } from 'react';
import Header from '../components/Header'; // Import the Header component
import { Box, Card, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions, Avatar } from '@mui/material';
import '../styles/ProfilePage.css';
import { getTripsByUserId, deleteTrip } from '../api/tripApi'; // Import the API function
import { Link, useNavigate } from 'react-router-dom';

interface Trip {
    id: number;
    destination: string;
    description: string;
    userId: number;
    startDate: ReactNode;
    endDate: ReactNode;
}

interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    username: string;
    // other details
}

const ProfilePage: React.FC = () => {
    const [isAccountDetailsExpanded, setIsAccountDetailsExpanded] = useState(false); // State to toggle account details visibility
    const [user, setUser] = useState<User | null>(null);
    const [userTrips, setUserTrips] = useState<Trip[]>([]); // State to store trips
    const [loading, setLoading] = useState<boolean>(true); // Loading state for API call
    const [dialogOpen, setDialogOpen] = useState<boolean>(false); // Track dialog open state
    const [selectedTrip, setSelectedTrip] = useState<Trip | null>(null); // Store the selected trip
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState<boolean>(false); // Track delete confirmation dialog

    const navigate = useNavigate();

    useEffect(() => {
        // Retrieve the user object from sessionStorage
        const storedUser = sessionStorage.getItem('user');
        if (storedUser) {
            const parsedUser: User = JSON.parse(storedUser);
            setUser(parsedUser);
            fetchUserTrips(parsedUser.id); // Fetch user trips using the userId
        }
    }, []);

    // Function to fetch trips for a user from the API
    const fetchUserTrips = async (userId: number) => {
        try {
            setLoading(true); // Set loading state to true
            const trips = await getTripsByUserId(userId); // Fetch trips using the API
            setUserTrips(trips); // Set the fetched trips in the state
        } catch (error) {
            console.error('Error fetching trips:', error);
        } finally {
            setLoading(false); // Set loading state to false after API call
        }
    };
    const navigateToProfile = () => {
        console.log('Already on profile page');
    };

    const logout = () => {
        console.log('Logout'); // Add your logout logic here
        sessionStorage.removeItem('user'); // Remove user data from sessionStorage on logout
    };

    const handleAddTrip = () => {
        window.location.href = '/create-trip'; // Redirect to CreateTrip page
    };

    const toggleAccountDetails = () => {
        setIsAccountDetailsExpanded(!isAccountDetailsExpanded); // Toggle expansion
    };

    const handleOpenDialog = (trip: Trip) => {
        setSelectedTrip(trip);
        setDialogOpen(true); // Open the dialog when a trip is selected
    };

    const handleCloseDialog = () => {
        setDialogOpen(false); // Close the dialog
        setSelectedTrip(null); // Reset the selected trip
    };

    const handleEditTrip = (id: number) => {
        if (selectedTrip) {
            // Redirect to the edit page for the selected trip
            navigate(`/edit-trip/${id}`);
        }
    };

    const handleDeleteTrip = async (id: number | undefined) => {
        if (selectedTrip) {
            // Here you can call the API to delete the trip
            console.log(`Deleting trip with ID: ${selectedTrip.id}`);
            // Close the dialog after deleting
            await deleteTrip(id);
            setDialogOpen(false);
            setSelectedTrip(null);
            setConfirmDeleteOpen(false);
            const storedUser = sessionStorage.getItem('user');
            if (storedUser) {
                const parsedUser: User = JSON.parse(storedUser);
                fetchUserTrips(parsedUser.id); // Fetch user trips using the userId
            }
        }
    };

    const handleConfirmDeleteOpen = () => {
        setConfirmDeleteOpen(true);
    };

    const handleConfirmDeleteClose = () => {
        setConfirmDeleteOpen(false);
    };

    return (
        <div className="profile-page-container">
            <Header
                isAddPost={true}
                isUserProfile={false}
                addPost={handleAddTrip}
                isLogout={true}
                logout={() => {
                    sessionStorage.removeItem('user');
                    window.location.href = '/';
                }}
            />

                <div className='avatar-container'>
                    <Avatar
                        alt={`${user?.firstName} ${user?.lastName}`}
                        src={`https://api.dicebear.com/9.x/avataaars-neutral/svg?seed=${user?.firstName}`}
                        sx={{ width: 64, height: 64, marginRight: 2 }}
                        component="div" // Adding the 'component' prop to specify the element type
                        >
                        {/* Fallback to initials */}
                        {user && `${user.firstName[0]}${user.firstName[0]}`}
                    </Avatar>
                </div>

            <Box className="profile-details-container">
                <Typography variant="h4" align="center" marginBottom={2} color="primary">
                    Welcome, {user?.firstName}!
                </Typography>

                <Box
                    className={`account-details ${isAccountDetailsExpanded ? 'expanded' : ''}`}
                    onClick={toggleAccountDetails}
                >
                    <Typography variant="h6" fontWeight="bold">
                        Account Details
                    </Typography>
                    {isAccountDetailsExpanded && (
                        <>
                            <Typography>Name: {user?.firstName} {user?.lastName}</Typography>
                            <Typography>Username: @{user?.username}</Typography>
                            <Typography>Email: {user?.email}</Typography>
                        </>
                    )}
                </Box>
            </Box>

            {/* Trips List */}
            <Box className="trips-list">
                {loading ? (
                    <Typography variant="h6" align="center">Loading trips...</Typography>
                ) : userTrips.length > 0 ? (
                    userTrips.map((trip) => (
                        <Card key={trip.id} className="trip-card">
                            <Typography variant="h6" fontWeight="bold">{trip.destination}</Typography>
                            <Typography variant="body1" color="textSecondary">{trip.description}</Typography>

                            <Box display="flex" justifyContent="space-between" width="100%" marginTop={1}>
                                <Typography variant="body2">Start: {trip.startDate}</Typography>
                                <Typography variant="body2">End: {trip.endDate}</Typography>
                            </Box>

                            <Button
                                variant="contained"
                                // color="primary"
                                size="small"
                                className="view-more-button"
                                style={{ marginTop: '10px' }}
                                onClick={() => handleOpenDialog(trip)} // Open dialog when clicked
                            >
                                View More
                            </Button>
                        </Card>
                    ))
                ) : (
                    <Box textAlign="center" marginTop={4}>
                        <img
                            src="https://media.istockphoto.com/id/1327746564/vector/friends-going-on-road-trip-together.jpg?s=612x612&w=0&k=20&c=-e-DzTmY-skqrZzjGkJ4u_vUoNK-3e9LE9_PhAeGFFo="
                            alt="No trips"
                            className="no-trips-image"
                        />
                        <Typography variant="h6" className='no-trip'>
                            No trips to display. Click "+" to add a new trip!
                        </Typography>
                    </Box>
                )}
            </Box>

            {/* Dialog to show detailed trip information */}
            <Dialog open={dialogOpen} onClose={handleCloseDialog}>
                <DialogTitle>
                    <Box display="flex" justifyContent="center" width="100%">
                        <Typography variant="h5" color="primary" fontWeight="bold">
                            Destination: {selectedTrip?.destination}
                        </Typography>
                    </Box>
                </DialogTitle>
                <DialogContent>
                    <Typography variant="body1" color="textSecondary" paragraph marginTop="20px">
                        <strong>Description:</strong> {selectedTrip?.description}
                    </Typography>

                    <Box display="flex" flexDirection="column" marginBottom={2}>
                        <Typography variant="body2" color="textSecondary">
                            <strong>Start Date:</strong> {selectedTrip?.startDate}
                        </Typography>
                        <Typography variant="body2" color="textSecondary" marginTop={1}>
                            <strong>End Date:</strong> {selectedTrip?.endDate}
                        </Typography>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Box display="flex" justifyContent="space-between" width="100%">
                        <Box flex={1}>
                            <Button
                                onClick={handleCloseDialog}
                                color="primary"
                                fullWidth
                                variant="outlined"
                                size="large"
                            >
                                Close
                            </Button>
                        </Box>
                        <Box flex={1} marginLeft={1}>
                            <Button
                                onClick={() => handleEditTrip(selectedTrip?.id)}
                                color="primary"
                                fullWidth
                                variant="contained"
                                size="large"
                            >
                                Edit
                            </Button>
                        </Box>
                    </Box>
                    {/* Delete Button */}
                    <Box marginTop={1} width="100%">
                        <Button
                            onClick={() => handleConfirmDeleteOpen()} // Open confirmation dialog
                            color="error"
                            fullWidth
                            variant="contained"
                            size="large"
                        >
                            Delete Trip
                        </Button>
                    </Box>
                </DialogActions>
            </Dialog>

            {/* Confirmation Dialog for Delete */}
            <Dialog open={confirmDeleteOpen} onClose={handleConfirmDeleteClose}>
                <DialogTitle className="delete-confirmation-dialog">
                    <Typography variant="h6" color="primary">
                        Are you sure you want to delete this trip?
                    </Typography>
                </DialogTitle>
                <DialogContent className="delete-confirmation-dialog">
                    <Typography variant="body1" color="textSecondary">
                        This action cannot be undone. Deleting this trip will remove all associated details permanently.
                    </Typography>
                </DialogContent>
                <DialogActions className="delete-confirmation-dialog">
                    <Button onClick={handleConfirmDeleteClose} className="MuiButton-outlinedPrimary">
                        Cancel
                    </Button>
                    <Button onClick={() => handleDeleteTrip(selectedTrip?.id)} className="MuiButton-containedError">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>



        </div>
    );
};

export default ProfilePage;
