import { atom } from 'jotai';
import { atomWithStorage } from 'jotai/utils';

// Theme Atom: Manages the application's theme (e.g., 'light' or 'dark')
// Using atomWithStorage to persist the theme preference
export const themeAtom = atomWithStorage<'light' | 'dark'>('appTheme', 'light');

// Session Atom: Stores user session information (e.g., authentication token, user details)
// Initial state can be null or an empty object, indicating no active session
export const sessionAtom = atom<null | { token: string; user: any }>(null);

// Is Bootstrapped Atom: Indicates if the application's initial setup (e.g., splash screen, data loading) is complete
export const isBootstrappedAtom = atom<boolean>(false);

// Example of a derived atom (read-only calculation)
export const isAuthenticatedAtom = atom((get) => !!get(sessionAtom)?.token);

// Example of an atom with effects (for logging or side-effects, typically handled in custom hooks)
// For instance, a custom hook could observe themeAtom and update native UI elements or log changes.
// This is a conceptual example; actual effects are usually in React components or custom hooks.
export const logThemeChangeAtom = atom(
  null, // This is a write-only atom
  (get, set, newTheme: 'light' | 'dark') => {
    set(themeAtom, newTheme);
    console.log('Theme changed to:', newTheme);
    // Further side-effects like updating native modules could go here or in a useEffect hook
  }
);