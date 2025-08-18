import EncryptedStorage from 'react-native-encrypted-storage';
import { jotaiStore } from '../../../app/store';
import { sessionAtom, userAtom } from '../state/authAtoms';
import { httpRequest } from '../../../shared/api/http';
import {
  LoginCredentials,
  RegisterCredentials,
  AuthResponseData,
  OAuthLoginRequest,
  OAuthLoginResponse,
} from '../types/auth.types';

const SESSION_STORAGE_KEY = 'heybro_session';

// --- Standard Auth APIs ---

export const loginApi = async (credentials: LoginCredentials): Promise<AuthResponseData> => {
  return httpRequest<AuthResponseData>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
};

export const registerApi = async (credentials: RegisterCredentials): Promise<AuthResponseData> => {
  return httpRequest<AuthResponseData>('/api/users', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
};

// --- OAuth API ---

export const loginWithOAuthApi = async (
  credentials: OAuthLoginRequest
): Promise<OAuthLoginResponse> => {
  return httpRequest<OAuthLoginResponse>('/api/oauth2/authorization', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
};


// --- Logout ---

export const logoutUser = async (): Promise<void> => {
  console.log('Logging out user...');

  try {
    const session = jotaiStore.get(sessionAtom);  
    if (session.isAuthenticated && session.accessToken) {
        await httpRequest('/api/user/logout', {
          method: 'POST',
        });
        console.log('Server notified of logout.');
      }
    
  } catch (error) {
    console.error('Failed to notify server of logout:', error);
  } finally {
    console.log('Clearing local session...');
    jotaiStore.set(sessionAtom, {
      isAuthenticated: false,
      accessToken: null,
      refreshToken: null,
    });
    jotaiStore.set(userAtom, null);

    try {
      await EncryptedStorage.removeItem(SESSION_STORAGE_KEY);
      console.log('Session removed from EncryptedStorage.');
    } catch (error) {
      console.error('Failed to remove session from EncryptedStorage:', error);
    }
  }
};
