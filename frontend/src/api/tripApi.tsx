import axiosInstance from '../config/axios';
import { TripDTO } from '../data/TripDTO';

const API_URL = '/api/trips';

// API functions for Trip

// Get all trips
export const getAllTrips = async (): Promise<TripDTO[]> => {
  try {
    const response = await axiosInstance.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching trips:', error);
    throw error;
  }
};

// Get a trip by its ID
export const getTripById = async (id: number): Promise<TripDTO | null> => {
  try {
    const response = await axiosInstance.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching trip by ID:', error);
    return null;
  }
};

// Get trips by user ID
export const getTripsByUserId = async (userId: number): Promise<TripDTO[]> => {
  try {
    const response = await axiosInstance.get(`${API_URL}/user/${userId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching trips for user:', error);
    throw error;
  }
};

// Create a new trip
export const createTrip = async (trip: TripDTO): Promise<TripDTO> => {
  try {
    const response = await axiosInstance.post(API_URL, trip);
    return response.data;
  } catch (error) {
    console.error('Error creating trip:', error);
    throw error;
  }
};

// Update an existing trip
export const updateTrip = async (id: number, trip: TripDTO): Promise<TripDTO | null> => {
  try {
    const response = await axiosInstance.put(`${API_URL}/${id}`, trip);
    return response.data;
  } catch (error) {
    console.error('Error updating trip:', error);
    return null;
  }
};

// Delete a trip by ID
export const deleteTrip = async (id: number): Promise<void> => {
  try {
    await axiosInstance.delete(`${API_URL}/${id}`);
  } catch (error) {
    console.error('Error deleting trip:', error);
    throw error;
  }
};
