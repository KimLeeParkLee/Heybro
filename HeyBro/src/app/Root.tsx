import '../shared/api/logFetch'; // Activate fetch logger
import React, { useEffect, useState } from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { NavigationContainer } from '@react-navigation/native';
import { Provider as JotaiProvider } from 'jotai';
import { StatusBar, ActivityIndicator } from 'react-native';
import { jotaiStore } from './store'; // 중앙 스토어 import

import { View } from '../shared/components/ui/View';
import RootNavigator from './navigation/RootNavigator';
import { useAuth } from '../features/auth/hooks/useAuth';

/**
 * This component handles the session restoration logic.
 * It shows a loading indicator while checking for a stored session
 * and then renders the main navigator.
 */
function AppContent() {
  const { restoreSession } = useAuth();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const bootstrapAsync = async () => {
      try {
        await restoreSession();
      } finally {
        setIsLoading(false);
      }
    };

    bootstrapAsync();
  }, [restoreSession]);

  if (isLoading) {
    // You can replace this with a proper splash screen component later
    return (
      <View className="flex-1 items-center justify-center">
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return <RootNavigator />;
}

function Root(): React.JSX.Element {
  const isDarkMode = false; // This will be managed by a Jotai themeAtom later

  const backgroundStyle = {
    backgroundColor: isDarkMode ? '#1a202c' : '#f7fafc',
  };

  return (
    <JotaiProvider store={jotaiStore}>
      <SafeAreaProvider>
        <GestureHandlerRootView style={{ flex: 1 }}>
          <NavigationContainer>
            <StatusBar
              barStyle={isDarkMode ? 'light-content' : 'dark-content'}
              backgroundColor={backgroundStyle.backgroundColor}
            />
            <AppContent />
          </NavigationContainer>
        </GestureHandlerRootView>
      </SafeAreaProvider>
    </JotaiProvider>
  );
}

export default Root;
