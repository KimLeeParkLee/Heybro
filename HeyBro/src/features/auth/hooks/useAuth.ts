import { useAtom } from 'jotai';
import EncryptedStorage from 'react-native-encrypted-storage';
import { sessionAtom, userAtom, Session, User } from '../state/authAtoms';
import {
  loginApi,
  registerApi,
  logoutUser,
  loginWithOAuthApi,
} from '../services/authService';
import {
  LoginCredentials,
  RegisterCredentials,
  AuthResponseData,
  OAuthLoginRequest,
  OAuthNewUserData,
} from '../types/auth.types';

const SESSION_STORAGE_KEY = 'heybro_session';

// Type guard to check if the response is for a new user
function isNewUser(data: any): data is OAuthNewUserData {
  return data && data.is_new_user === true;
}

export const useAuth = () => {
  const [session, setSession] = useAtom(sessionAtom);
  const [, setUser] = useAtom(userAtom);

  const handleAuthSuccess = async (authData: AuthResponseData) => {
    const { access_token, refresh_token, ...userData } = authData;
    const newSession: Session = {
      isAuthenticated: true,
      accessToken: access_token.replace('Bearer ', ''),
      refreshToken: refresh_token,
    };
    setSession(newSession);
    setUser(userData as User);
    await EncryptedStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(newSession));
  };

  // --- Public Hook Methods ---

  const login = async (credentials: LoginCredentials) => {
    const authData = await loginApi(credentials);
    await handleAuthSuccess(authData);
  };

  const register = async (credentials: RegisterCredentials) => {
    const authData = await registerApi(credentials);
    await handleAuthSuccess(authData);
  };

  const loginWithOAuth = async (
    credentials: OAuthLoginRequest
  ): Promise<OAuthNewUserData | void> => {
    const response = await loginWithOAuthApi(credentials);
    if (isNewUser(response)) {
      // It's a new user, return the partial data so the UI can navigate
      // to the registration completion screen.
      return response;
    }
    // It's an existing user, proceed with login.
    await handleAuthSuccess(response);
  };

  const logout = async () => {
    await logoutUser();
  };

  const restoreSession = async () => {
    try {
      const storedSession = await EncryptedStorage.getItem(SESSION_STORAGE_KEY);
      if (storedSession) {
        const parsedSession: Session = JSON.parse(storedSession);
        setSession(parsedSession);
      }
    } catch (error) {
      console.error('Failed to restore session:', error);
      await logout();
    }
  };

  return {
    ...session,
    login,
    register,
    loginWithOAuth,
    logout,
    restoreSession,
  };
};
