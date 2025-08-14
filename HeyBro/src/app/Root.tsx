import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { NavigationContainer } from '@react-navigation/native';
import { Provider as JotaiProvider } from 'jotai';
import { StatusBar } from 'react-native';

// Import the UI primitive wrappers
import { View } from '../shared/components/ui/View';
import { Text } from '../shared/components/ui/Text';
import RootNavigator from '../app/navigation/RootNavigator'; // Add this import

function Root(): React.JSX.Element {
  // Determine initial dark mode based on system preference or a default
  // For now, let's assume a light theme by default, and dark mode can be toggled later.
  const isDarkMode = false; // This will be managed by Jotai themeAtom later

  const backgroundStyle = {
    backgroundColor: isDarkMode ? '#1a202c' : '#f7fafc', // Using colors from tailwind.config.js
    flex: 1,
  };

  return (
    <JotaiProvider>
      <SafeAreaProvider>
        <GestureHandlerRootView style={{ flex: 1 }}>
          <NavigationContainer>
            <StatusBar
              barStyle={isDarkMode ? 'light-content' : 'dark-content'}
              backgroundColor={backgroundStyle.backgroundColor}
            />
            {/* Replace the placeholder content with RootNavigator */}
            <RootNavigator />
          </NavigationContainer>
        </GestureHandlerRootView>
      </SafeAreaProvider>
    </JotaiProvider>
  );
}

export default Root;
