import React, { useState } from 'react';
import '../styles/SignUpPage.css';
import Header from '../components/Header';
import { createUser } from '../api/userApi';
import { Box, Typography, TextField, Button, CircularProgress } from '@mui/material';
import { AxiosError } from 'axios';
import { useNavigate } from 'react-router-dom';

interface FormData {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

const SignUpPage: React.FC = () => {
  const [formData, setFormData] = useState<FormData>({
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });

  const [error, setError] = useState<string>('');
  const [, setSuccessMessage] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false); // Loading state for button

  const navigate = useNavigate(); // For redirecting after successful login

  // Handle form input changes
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Form validation
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords don't match!");
      return;
    }
    if (!formData.firstName || !formData.lastName || !formData.username || !formData.email || !formData.password || !formData.confirmPassword) {
      setError("All fields are required.");
      return;
    }

    setLoading(true);
    setError('');  // Clear any previous errors

    try {
      const response = await createUser(formData);  // Call the createUser API
      if (response.status === 201) {
        setSuccessMessage('User created successfully! Please log in.');
        setFormData({
          firstName: '',
          lastName: '',
          username: '',
          email: '',
          password: '',
          confirmPassword: '',
        });
        setError('');
        setTimeout(() => {
          navigate('/login', { state: { successMessage: 'User created successfully! Please log in.' } });
        }, 1000); // Small delay to allow user to see success message
      }
    } catch (error) {
      // Specify error type as AxiosError to ensure proper typing
      const axiosError = error as AxiosError;
      setError(axiosError.message || 'Error registering user. Please try again.');
      console.error('Error registering user:', axiosError.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header isAddPost={false} isUserProfile={false} />
      <Box className="signup-page"
        sx={{
          width: "100%",
          height: "100vh",
          backgroundImage: 'url(../src/images/trip_4.jpg)', 
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          display: 'flex', 
          justifyContent: 'center', 
          alignItems: 'center',
        }}>
        <Box
          className="signUp-container"
          sx={{
            backgroundColor: 'rgba(255, 255, 255, 0.8)', 
            borderRadius: 2, 
            padding: 4, 
            boxShadow: 4, 
            width: "100%", 
            maxWidth: "500px", // Optional: Adjust width of the form
          }}
        >
          <Typography variant="h4" gutterBottom sx={{ color: "#333", mb: 3 }}>
            Sign-Up
          </Typography>

          {/* Success Message */}
          {/* {successMessage && <p className="success">{successMessage}</p>}  */}

          {/* Error Message */}
          {error && <Typography color="error">{error}</Typography>}

          <form onSubmit={handleSubmit}>
            <TextField
              label="First Name"
              name="firstName"
              variant="outlined"
              fullWidth
              value={formData.firstName}
              onChange={handleChange}
              sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }} // Adjust label position
            />
            <TextField
              label="Last Name"
              name="lastName"
              variant="outlined"
              fullWidth
              value={formData.lastName}
              onChange={handleChange}
              sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }}
            />
            <TextField
              label="Email"
              name="email"
              variant="outlined"
              fullWidth
              value={formData.email}
              onChange={handleChange}
              sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }}
            />
            <TextField
              label="Username"
              name="username"
              variant="outlined"
              fullWidth
              value={formData.username}
              onChange={handleChange}
              sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }}
            />
            <TextField
              label="Password"
              name="password"
              type="password"
              variant="outlined"
              fullWidth
              value={formData.password}
              onChange={handleChange}
              sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }}
            />
            <TextField
              label="Confirm Password"
              name="confirmPassword"
              type="password"
              variant="outlined"
              fullWidth
              value={formData.confirmPassword}
              onChange={handleChange}
              sx={{ mb: 2, '& .MuiInputLabel-root': { top: '-8px' } }}
            />

            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              disabled={loading}
              sx={{ mb: 2 }}
            >
              {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign Up'}
            </Button>
          </form>
          <Typography variant="body2" color="textSecondary" align="center" sx={{ mt: 2 }}>
            Alredy have an account? <a href="/login">Login</a>
          </Typography>
        </Box>
      </Box>
    </>
  );
};

export default SignUpPage;
