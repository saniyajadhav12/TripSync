import axiosInstance from '../config/axios';
import { ActivityDTO } from '../data/ActivityDTO';

const API_URL = '/api/activities';

// API functions for Activity

// Get all activities
export const getAllActivities = async (): Promise<ActivityDTO[]> => {
  try {
    const response = await axiosInstance.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching activities:', error);
    throw error;
  }
};

// Get an activity by its ID
export const getActivityById = async (id: number): Promise<ActivityDTO | null> => {
  try {
    const response = await axiosInstance.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching activity by ID:', error);
    return null;
  }
};

// Get activities by trip ID
export const getActivitiesByTripId = async (tripId: number): Promise<ActivityDTO[]> => {
  try {
    const response = await axiosInstance.get(`${API_URL}/trip/${tripId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching activities for trip:', error);
    throw error;
  }
};

// Create a new activity
export const createActivity = async (activity: ActivityDTO): Promise<ActivityDTO> => {
  try {
    const response = await axiosInstance.post(API_URL, activity);
    return response.data;
  } catch (error) {
    console.error('Error creating activity:', error);
    throw error;
  }
};

// Update an existing activity
export const updateActivity = async (id: number, activity: ActivityDTO): Promise<ActivityDTO | null> => {
  try {
    const response = await axiosInstance.put(`${API_URL}/${id}`, activity);
    return response.data;
  } catch (error) {
    console.error('Error updating activity:', error);
    return null;
  }
};

// Delete an activity by ID
export const deleteActivity = async (id: number): Promise<void> => {
  try {
    await axiosInstance.delete(`${API_URL}/${id}`);
  } catch (error) {
    console.error('Error deleting activity:', error);
    throw error;
  }
};
