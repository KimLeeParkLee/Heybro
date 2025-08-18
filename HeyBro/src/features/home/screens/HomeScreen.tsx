import React from 'react';
import { useAuth } from '../../auth/hooks/useAuth';
import { View, Text, Button } from '../../../shared/components/ui';

const HomeScreen = () => {
  const { logout } = useAuth();

  return (
    <View className="flex-1 items-center justify-center bg-white dark:bg-dark">
      <Text className="text-black dark:text-white text-2xl">Home Screen</Text>
      <Button title="Logout" onPress={logout} />
    </View>
  );
};

export default HomeScreen;
