// TypeScript interface for TripDTO (based on your controller)
export interface TripDTO {
    id: number;
    destination: string | undefined;
    description: string | undefined;
    startDate: string | undefined;
    endDate: string | undefined;
    userId: number | undefined;
  }