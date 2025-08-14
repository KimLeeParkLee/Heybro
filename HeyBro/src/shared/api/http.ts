import { getAtom } from 'jotai/utils';
import { sessionAtom } from '../atoms/app.atom'; // Assuming app.atom.ts is in shared/atoms

// Define a custom error class for standardized error handling
export class HttpError extends Error {
  statusCode: number;
  data: any;

  constructor(message: string, statusCode: number, data: any = null) {
    super(message);
    this.name = 'HttpError';
    this.statusCode = statusCode;
    this.data = data;
  }
}

// Base URL for your API
const API_BASE_URL = 'https://api.example.com'; // Replace with your actual API base URL

interface RequestOptions extends RequestInit {
  // Add any custom options here, e.g., for error handling flags
  handleError?: boolean;
}

export async function httpRequest<T>(
  endpoint: string,
  options?: RequestOptions
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  const headers = {
    'Content-Type': 'application/json',
    ...options?.headers,
  };

  // Inject token from sessionAtom if available
  const currentSession = getAtom(sessionAtom); // This is a conceptual way to get atom value outside React component
  if (currentSession?.token) {
    headers['Authorization'] = `Bearer ${currentSession.token}`;
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorData: any = null;
    try {
      errorData = await response.json();
    } catch (parseError) {
      // If response is not JSON, use text or status
      errorData = await response.text();
    }

    // Standardize error
    const errorMessage = errorData?.message || response.statusText || 'Something went wrong';
    throw new HttpError(errorMessage, response.status, errorData);
  }

  // Handle cases where response might be empty (e.g., 204 No Content)
  if (response.status === 204) {
    return null as T; // Or handle as appropriate for your API
  }

  return response.json();
}

// Example usage (can be moved to a service layer)
// async function fetchUserProfile() {
//   try {
//     const user = await httpRequest<{ id: string; name: string }>('/user/profile');
//     console.log('User Profile:', user);
//     return user;
//   } catch (error) {
//     if (error instanceof HttpError) {
//       console.error('HTTP Error:', error.message, error.statusCode, error.data);
//       // Here you would typically show a toast or dialog
//     } else {
//       console.error('Unknown Error:', error);
//     }
//     throw error; // Re-throw to allow calling component to handle
//   }
// }
