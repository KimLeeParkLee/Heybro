import { httpRequest } from '../../../shared/api/http';
import {
  LoginCredentials,
  RegisterCredentials,
  AuthResponseData,
  OAuthLoginRequest,
  OAuthNewUserData,
} from '../types/auth.types';

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
  credentials: OAuthLoginRequest,
): Promise<AuthResponseData | OAuthNewUserData> => {
  const { provider, oauth_token } = credentials;

  // Backend expects 'authorization_code' for Google and 'oauth_token' for others.
  const payload = {
    provider,
    oauth_token 
  };

  const responseData = await httpRequest<any>('/api/auth/oauth2/authorization', {
    method: 'POST',
    body: JSON.stringify(payload),
  });

  // Log the actual response data to debug
  console.log('OAuth Response Data:', JSON.stringify(responseData, null, 2));

  // After httpRequest, responseData is the `data` part of the API response.
  // A new user is identified by the presence of `oauth_token` in the data.
  if (responseData?.oauth_token) {
    return {
      is_new_user: true,
      ...responseData,
    };
  }

  // An existing user is identified by the presence of `access_token`.
  if (responseData?.access_token) {
    return responseData as AuthResponseData;
  }

  // If neither token is present, the response is unexpected.
  throw new Error('Unexpected response from OAuth login');
};


// --- Logout ---

/**
 * Notifies the server that the user is logging out.
 * This function ONLY handles the API call. State and storage are managed in useAuth hook.
 */
export const logoutApi = async (): Promise<void> => {
  // httpRequest will automatically include the auth token.
  // We don't care about the response, so we don't return anything.
  await httpRequest('/api/auth/logout', {
    method: 'POST',
  });
};
