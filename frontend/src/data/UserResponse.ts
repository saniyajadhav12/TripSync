// Mirrors edu.neu.csye6200.tripsync.dto.UserResponse (never includes the password)
export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  username: string;
}
