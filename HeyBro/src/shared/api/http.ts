import EncryptedStorage from 'react-native-encrypted-storage';
import { jotaiStore } from '../../app/store';
import { sessionAtom, Session } from '../../features/auth/state/authAtoms';
import { logoutUser } from '../../features/auth/services/authService';
import { ApiResponse } from '../types/api';

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

const API_BASE_URL = 'https://kimleeparklee.shop'; 
const SESSION_STORAGE_KEY = 'heybro_session';

// --- Token Refresh Logic ---
let isRefreshing = false;

async function handleTokenRefresh(): Promise<string | null> {
  // ... (previous token refresh logic remains the same)
  if (isRefreshing) {
    return new Promise((resolve) => {
      const interval = setInterval(() => {
        const updatedSession = jotaiStore.get(sessionAtom);
        if (!isRefreshing) {
          clearInterval(interval);
          resolve(updatedSession.accessToken);
        }
      }, 100);
    });
  }

  isRefreshing = true;

  try {
    const sessionString = await EncryptedStorage.getItem(SESSION_STORAGE_KEY);
    if (!sessionString) throw new Error('No session found');

    const session: Session = JSON.parse(sessionString);
    const refreshToken = session.refreshToken;

    if (!refreshToken) throw new Error('No refresh token available');

    const response = await fetch(`${API_BASE_URL}/api/user/reissue`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refresh_token: refreshToken }),
    });

    if (!response.ok) {
      await logoutUser();
      throw new Error('Failed to refresh token, status: ' + response.status);
    }

    const responseData: ApiResponse<{ access_token: string }> = await response.json();

    if (!responseData.success) {
      await logoutUser();
      throw new Error(responseData.message || 'Failed to refresh token');
    }

    const newAccessToken = responseData.data.access_token;

    const updatedSession: Session = { ...session, accessToken: newAccessToken };
    jotaiStore.set(sessionAtom, updatedSession);
    await EncryptedStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(updatedSession));

    return newAccessToken;
  } catch (error) {
    console.error('Token refresh error:', error);
    await logoutUser();
    return null;
  } finally {
    isRefreshing = false;
  }
}


// --- Main HTTP Request Function ---
interface RequestOptions extends RequestInit {
  headers?: Record<string, string>;
}

export async function httpRequest<T>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<T> {
  const makeRequest = async (token: string | null): Promise<T> => {
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, { ...options, headers });
    const responseData: ApiResponse<T> = await response.json();

    if (!response.ok || !responseData.success) {
      throw new HttpError(
        responseData.message || 'Request failed',
        response.status,
        responseData.data
      );
    }
    
    return responseData.data;
  };

  try {
    const initialToken = jotaiStore.get(sessionAtom).accessToken;
    return await makeRequest(initialToken);
  } catch (error) {
    if (error instanceof HttpError && error.statusCode === 401) {
      console.log('Access token expired. Attempting to refresh...');
      const newAccessToken = await handleTokenRefresh();
      if (newAccessToken) {
        console.log('Token refreshed successfully. Retrying original request...');
        return await makeRequest(newAccessToken);
      }
    }
    throw error;
  }
}
