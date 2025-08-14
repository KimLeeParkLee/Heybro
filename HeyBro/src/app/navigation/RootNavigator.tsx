import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Text, View } from '../../shared/components/ui'; // Assuming these are correctly exported from ui/index.ts

// Define your screen types for better type safety
// This will eventually be merged into navigation.d.ts
type AuthStackParamList = {
  Login: undefined;
  Register: undefined;
};

type MainTabParamList = {
  Home: undefined;
  Settings: undefined;
};

const AuthStack = createNativeStackNavigator<AuthStackParamList>();
const MainTab = createBottomTabNavigator<MainTabParamList>();

// Placeholder Screens
const LoginScreen = () => (
  <View className="flex-1 items-center justify-center bg-white dark:bg-dark">
    <Text className="text-black dark:text-white text-2xl">Login Screen</Text>
  </View>
);

const RegisterScreen = () => (
  <View className="flex-1 items-center justify-center bg-white dark:bg-dark">
    <Text className="text-black dark:text-white text-2xl">Register Screen</Text>
  </View>
);

const HomeScreen = () => (
  <View className="flex-1 items-center justify-center bg-white dark:bg-dark">
    <Text className="text-black dark:text-white text-2xl">Home Screen</Text>
  </View>
);

const SettingsScreen = () => (
  <View className="flex-1 items-center justify-center bg-white dark:bg-dark">
    <Text className="text-black dark:text-white text-2xl">Settings Screen</Text>
  </View>
);

const AuthStackNavigator = () => {
  return (
    <AuthStack.Navigator screenOptions={{ headerShown: false }}>
      <AuthStack.Screen name="Login" component={LoginScreen} />
      <AuthStack.Screen name="Register" component={RegisterScreen} />
    </AuthStack.Navigator>
  );
};

const MainTabNavigator = () => {
  return (
    <MainTab.Navigator>
      <MainTab.Screen name="Home" component={HomeScreen} />
      <MainTab.Screen name="Settings" component={SettingsScreen} />
    </MainTab.Navigator>
  );
};

const RootNavigator = () => {
  // In a real app, you'd use Jotai's sessionAtom to determine if the user is authenticated
  const isAuthenticated = false; // Placeholder for now

  return isAuthenticated ? <MainTabNavigator /> : <AuthStackNavigator />;
};

export default RootNavigator;
