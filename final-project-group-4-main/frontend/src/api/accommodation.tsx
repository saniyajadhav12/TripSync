import axios from 'axios';
import { AccommodationDTO } from '../data/AccommodationDTO';

const API_URL = 'http://localhost:8080/api/accommodations';

// API functions for Accommodation

// Get all accommodations
export const getAllAccommodations = async (): Promise<AccommodationDTO[]> => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching accommodations:', error);
    throw error;
  }
};

// Get an accommodation by its ID
export const getAccommodationById = async (id: number): Promise<AccommodationDTO | null> => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching accommodation by ID:', error);
    return null;
  }
};

// Get accommodations by trip ID
export const getAccommodationsByTripId = async (tripId: number): Promise<AccommodationDTO[]> => {
  try {
    const response = await axios.get(`${API_URL}/trip/${tripId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching accommodations by trip ID:', error);
    throw error;
  }
};

// Create a new accommodation
export const createAccommodation = async (accommodation: AccommodationDTO): Promise<AccommodationDTO> => {
  try {
    const response = await axios.post(API_URL, accommodation);
    return response.data;
  } catch (error) {
    console.error('Error creating accommodation:', error);
    throw error;
  }
};

// Update an existing accommodation
export const updateAccommodation = async (id: number, accommodation: AccommodationDTO): Promise<AccommodationDTO | null> => {
  try {
    const response = await axios.put(`${API_URL}/${id}`, accommodation);
    return response.data;
  } catch (error) {
    console.error('Error updating accommodation:', error);
    return null;
  }
};

// Delete an accommodation by ID
export const deleteAccommodation = async (id: number): Promise<void> => {
  try {
    await axios.delete(`${API_URL}/${id}`);
  } catch (error) {
    console.error('Error deleting accommodation:', error);
    throw error;
  }
};
