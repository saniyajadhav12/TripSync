import { ActivityDTO } from './ActivityDTO';
import { AccommodationDTO } from './AccommodationDTO';

// Mirrors edu.neu.csye6200.tripsync.dto.TripDTO. Server-set fields are optional.
export interface TripDTO {
  id?: number;
  destination: string;
  description: string;
  startDate: string;
  endDate: string;
  userId: number;
  activities?: ActivityDTO[];
  accommodations?: AccommodationDTO[];
  createdAt?: string;
  updatedAt?: string;
}
