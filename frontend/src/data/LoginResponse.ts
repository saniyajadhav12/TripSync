import { UserResponse } from './UserResponse';

// Mirrors edu.neu.csye6200.tripsync.dto.LoginResponse
export interface LoginResponse {
  token: string;
  user: UserResponse;
}
