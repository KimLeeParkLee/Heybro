import EncryptedStorage from 'react-native-encrypted-storage';
import { jotaiStore } from '../../../app/store';
import {
  sessionAtom,
  userAtom,
  newOAuthUserAtom,
} from '../state/authAtoms';

const SESSION_STORAGE_KEY = 'heybro_session';

/**
 * Clears all authentication-related data from Jotai state and EncryptedStorage.
 * This is a non-hook utility function that can be called from anywhere in the app.
 */
export const clearAuthData = async () => {
  console.log('Clearing local auth data...');

  // Reset all auth-related atoms to their initial state
  jotaiStore.set(sessionAtom, {
    isAuthenticated: false,
    accessToken: null,
    refreshToken: null,
  });
  jotaiStore.set(userAtom, null);
  jotaiStore.set(newOAuthUserAtom, null);

  // Remove the session from persistent storage
  try {
    // Check if the item exists before trying to remove it to prevent errors.
    const session = await EncryptedStorage.getItem(SESSION_STORAGE_KEY);
    if (session) {
      await EncryptedStorage.removeItem(SESSION_STORAGE_KEY);
      console.log('Session removed from EncryptedStorage.');
    }
  } catch (error) {
    console.error('Failed to remove session from EncryptedStorage:', error);
  }
};
