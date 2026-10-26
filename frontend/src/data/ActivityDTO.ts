// Mirrors edu.neu.csye6200.tripsync.model.Category
export type Category =
  | 'Sightseeing'
  | 'Adventure'
  | 'Dining'
  | 'Shopping'
  | 'Leisure'
  | 'Entertainment'
  | 'Cultural_Experience'
  | 'Other';

// Mirrors edu.neu.csye6200.tripsync.dto.ActivityDTO. Server-set fields are optional.
export interface ActivityDTO {
  id?: number;
  title: string;
  category: Category;
  description: string;
  startDateTime: string;
  endDateTime: string;
  cost: number;
  tripId: number;
  createdAt?: string;
  updatedAt?: string;
}
