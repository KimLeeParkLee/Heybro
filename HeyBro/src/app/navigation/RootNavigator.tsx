import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { useAtom } from 'jotai';
import { sessionAtom, userAtom } from '../../features/auth/state/authAtoms';

// Screen Imports
import LoginScreen from '../../features/auth/screens/LoginScreen';
import RegisterScreen from '../../features/auth/screens/RegisterScreen';
import HomeScreen from '../../features/home/screens/HomeScreen';
import OnboardingScreen from '../../features/onboarding/screens/OnboardingScreen';
import OnboardingResultScreen from '../../features/onboarding/screens/OnboardingResultScreen';
import QnAScreen from '../../features/qna/screens/QnAScreen';
import RoutineScreen from '../../features/routine/screens/RoutineScreen';
import PointScreen from '../../features/point/screens/PointScreen';
import MyPageScreen from '../../features/user/screens/MyPageScreen';
import RoutineDetailScreen from '../../features/routine/screens/RoutineDetailScreen';

// Define your screen types for better type safety
export type AuthStackParamList = {
  Login: undefined;
  Register: {
    provider: string;
    email: string;
  } | undefined;
};

type MainTabParamList = {
  Home: undefined;
  QnA: undefined;
  Routine: undefined;
  Point: undefined;
  MyPage: undefined;
};

const AuthStack = createNativeStackNavigator<AuthStackParamList>();
const MainTab = createBottomTabNavigator<MainTabParamList>();

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
    <MainTab.Navigator screenOptions={{ headerShown: false }}>
      <MainTab.Screen name="Home" component={HomeScreen} />
      <MainTab.Screen name="QnA" component={QnAScreen} />
      <MainTab.Screen name="Routine" component={RoutineScreen} />
      <MainTab.Screen name="Point" component={PointScreen} />
      <MainTab.Screen name="MyPage" component={MyPageScreen} />
    </MainTab.Navigator>
  );
};


export type AppStackParamList = {
  Main: undefined;
  Onboarding: undefined;
  OnboardingResult: { user_type: string };
  RoutineDetail: { routine_element_id: number; routine_element_name: string };
};

const AppStack = createNativeStackNavigator<AppStackParamList>();

const RootNavigator = () => {
  const [session] = useAtom(sessionAtom);
  const [user] = useAtom(userAtom);

  // A user is only properly authenticated and loaded if both session and user data are present.
  // This prevents a flicker to the Onboarding screen during logout.
  if (!session.isAuthenticated || !user) {
    return <AuthStackNavigator />;
  }

  // Now that we know the user is not null, we can proceed.
  return (
    <AppStack.Navigator screenOptions={{ headerShown: false }}>
      {user.onboarding_completed ? (
        <>
          <AppStack.Screen name="Main" component={MainTabNavigator} />
          <AppStack.Screen name="RoutineDetail" component={RoutineDetailScreen} />
        </>
      ) : (
        <>
          <AppStack.Screen name="Onboarding" component={OnboardingScreen} />
          <AppStack.Screen name="OnboardingResult" component={OnboardingResultScreen} />
        </>
      )}
    </AppStack.Navigator>
  );
};


export default RootNavigator;
