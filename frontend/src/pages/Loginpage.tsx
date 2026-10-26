import React, { useState } from 'react';
import { authenticateUser } from '../api/userApi';
import '../styles/LoginPage.css';
import Header from '../components/Header';
import { Box, Container, Typography, TextField, Button, Snackbar, Alert } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import { AxiosError } from 'axios';

interface LoginData {
  username: string;
  password: string;
}

const LoginPage: React.FC = () => {
  const [formData, setFormData] = useState<LoginData>({
    username: '',
    password: '',
  });
  const [error, setError] = useState<string>('');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);

  const navigate = useNavigate();
  const location = useLocation();
  const successMessage = location.state?.successMessage || ''; // Access the success message passed via state

  // Show success message when redirected from SignUpPage
  React.useEffect(() => {
    if (successMessage) {
      setSnackbarOpen(true); // Open snackbar to show the message
    }
  }, [successMessage]);

  // Handle form input changes
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(''); // Reset error message before trying to authenticate

    try {
      const user = await authenticateUser(formData.username, formData.password);
      if (user) {
        navigate('/home');
      } else {
        setError('Invalid username or password');
      }
    } catch (error: unknown) {
      if (error instanceof AxiosError) {
        if (error.response) {
          setError(error.response.data.message || 'Error logging in. Please try again.');
        } else {
          setError('An unknown error occurred. Please try again later.');
        }
      } else if (error instanceof Error) {
        setError(error.message || 'Error logging in. Please try again.');
      } else {
        setError('An unknown error occurred. Please try again later.');
      }
      console.error('Login error:', error);
    }
  };

  // Handle snackbar close
  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  return (
    <>
      <Header isAddPost={false} isUserProfile={false} />

      <Box 
        className="login-page" // Apply the background image to the Box component
        sx={{width: "100%", height: "100vh",
          backgroundImage: 'url(../src/images/trip_1.jpg)', 
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          display: 'flex', 
          justifyContent: 'center', 
          alignItems: 'center',
        }}
      >
        <Container component="main" maxWidth="xs"> 
          <Box
            sx={{
              backgroundColor: 'rgba(255, 255, 255, 0.8)', 
              borderRadius: 2,
              padding: 4,
              boxShadow: 4, 
            }}
          >
            <Typography variant="h5" align="center" sx={{ mb: 2 }} style={{color: '#000000'}}>Login</Typography>
            
            {error && <Typography color="error" align="center" sx={{ mb: 2 }}>{error}</Typography>} {/* Display error messages */}

            <form onSubmit={handleSubmit}>
              <TextField
                label="Username"
                variant="outlined"
                fullWidth
                name="username"
                type="text"
                value={formData.username}
                onChange={handleChange}
                sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }} // Adjust label position
              />
              <TextField
                label="Password"
                variant="outlined"
                fullWidth
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }} // Adjust label position
              />
              <Button 
                type="submit"
                variant="contained"
                color="primary"
                fullWidth
                sx={{ mb: 2 }}
              >
                Login
              </Button>
              <Typography variant="body2" color="textSecondary" align="center" sx={{ mt: 2 }}>
                Don't have an account? <a href="/signup">Sign-up</a>
              </Typography>
            </form>
          </Box>
        </Container>
      </Box>

      {/* Snackbar for success message */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={4000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleSnackbarClose} severity="success" sx={{ width: '100%' }}>
          {successMessage}
        </Alert>
      </Snackbar>
    </>
  );
};

export default LoginPage;