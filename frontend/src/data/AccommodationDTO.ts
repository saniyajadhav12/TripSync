// Mirrors edu.neu.csye6200.tripsync.model.Type
export type AccommodationType =
  | 'Hostel'
  | 'Hotel'
  | 'Guesthouse'
  | 'Resort'
  | 'Camping'
  | 'Villa'
  | 'Farm_Stay';

// Mirrors edu.neu.csye6200.tripsync.dto.AccommodationDTO. Server-set fields are optional.
export interface AccommodationDTO {
  id?: number;
  name: string;
  address: string;
  type: AccommodationType;
  checkInDate: string;
  checkOutDate: string;
  review?: string;
  cost: number;
  tripId: number;
  createdAt?: string;
  updatedAt?: string;
}
