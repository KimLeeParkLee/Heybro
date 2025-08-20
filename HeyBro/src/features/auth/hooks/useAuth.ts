import { useAtom } from 'jotai';
import EncryptedStorage from 'react-native-encrypted-storage';
import {
  sessionAtom,
  userAtom,
  newOAuthUserAtom,
  Session,
  User,
} from '../state/authAtoms';
import {
  loginApi,
  registerApi,
  logoutApi,
  loginWithOAuthApi,
} from '../services/authService';
import {
  LoginCredentials,
  RegisterCredentials,
  AuthResponseData,
  OAuthLoginRequest,
  OAuthNewUserData,
} from '../types/auth.types';
import { clearAuthData } from '../utils/sessionManager';

const SESSION_STORAGE_KEY = 'heybro_session';

// Type guard to check if the response is for a new user
function isNewUser(
  data: AuthResponseData | OAuthNewUserData,
): data is OAuthNewUserData {
  return (data as OAuthNewUserData).is_new_user === true;
}

export const useAuth = () => {
  const [session, setSession] = useAtom(sessionAtom);
  const [user, setUser] = useAtom(userAtom);
  const [newOAuthUser, setNewOAuthUser] = useAtom(newOAuthUserAtom);

  const handleAuthSuccess = async (
    authData: AuthResponseData,
    onboardingCompleted: boolean,
  ) => {
    const { access_token, refresh_token, ...userData } = authData;
    const newSession: Session = {
      isAuthenticated: true,
      accessToken: access_token.replace('Bearer ', ''),
      refreshToken: refresh_token,
    };
    setSession(newSession);
    // API 응답(userData)과 클라이언트 상태(onboardingCompleted)를 조합하여 완전한 User 객체를 만듭니다.
    const userProfile: User = {
      ...userData,
      onboarding_completed: onboardingCompleted,
    };
    setUser(userProfile);
    await EncryptedStorage.setItem(
      SESSION_STORAGE_KEY,
      JSON.stringify(newSession),
    );
    // Clear any pending OAuth user data on successful login
    setNewOAuthUser(null);
  };

  const completeOnboarding = () => {
    if (user) {
      setUser({ ...user, onboarding_completed: true });
    }
  };

  /**
   * Updates the current user's state with new data.
   * @param updates A partial User object with the fields to update.
   */
  const updateUser = (updates: Partial<User>) => {
    setUser(prevUser => (prevUser ? { ...prevUser, ...updates } : null));
  };

  // --- Public Hook Methods ---

  const login = async (credentials: LoginCredentials) => {
    const authData = await loginApi(credentials);
    await handleAuthSuccess(authData, true);
  };

  const registerAndLogin = async (credentials: RegisterCredentials) => {
    await registerApi(credentials);
    if (!credentials.password) {
      throw new Error('Password is required for standard registration.');
    }
    const authData = await loginApi({
      email: credentials.email,
      password: credentials.password,
    });
    await handleAuthSuccess(authData, false);
  };

  const loginWithOAuth = async (
    credentials: OAuthLoginRequest,
  ): Promise<OAuthNewUserData | void> => {
    const response = await loginWithOAuthApi(credentials);
    if (isNewUser(response)) {
      // It's a new user, store data in atom and return to UI
      setNewOAuthUser(response);
      return response;
    }
    // It's an existing user, complete the login
    await handleAuthSuccess(response, true);
  };

  const completeOAuthRegistration = async (
    registrationData: Omit<RegisterCredentials, 'email' | 'provider' | 'oauth_token'>,
  ) => {
    if (!newOAuthUser) {
      throw new Error('No pending OAuth user data found for registration.');
    }

    // Step 1: Construct the final registration data including the email, provider, and token from the initial OAuth flow.
    const finalCredentials: RegisterCredentials = {
      ...registrationData,
      email: newOAuthUser.email,
      provider: newOAuthUser.provider,
      oauth_token: newOAuthUser.oauth_token,
    };

    // Step 2: Call the register API. This endpoint creates the user and returns session tokens directly.
    const authData = await registerApi(finalCredentials);

    // Step 3: Handle the successful login and session creation, marking the user for onboarding.
    // The second login call is not needed.
    await handleAuthSuccess(authData, false);
  };

  const logout = async () => {
    console.log('Attempting to log out user...');

    // Notify the server first.
    try {
      if (session.isAuthenticated) {
        await logoutApi();
        console.log('Server notified of logout.');
      }
    } catch (error) {
      console.error('Failed to notify server of logout:', error);
    }

    // Clear all local state and storage using the utility function.
    await clearAuthData();
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
      await clearAuthData();
    }
  };

  return {
    ...session,
    user, // Expose user data
    login,
    registerAndLogin,
    loginWithOAuth,
    completeOAuthRegistration,
    logout,
    restoreSession,
    completeOnboarding,
    updateUser, // Expose the new updater function
  };
};
