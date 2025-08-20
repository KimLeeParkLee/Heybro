import React from 'react';
import { View, Text, Button } from '../../../shared/components/ui';
import { useAuth } from '../../auth/hooks/useAuth';

const MyPageScreen = () => {
  const { logout } = useAuth();
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>My Page Screen</Text>
      <Button title="Logout" onPress={logout} />
    </View>
  );
};

export default MyPageScreen;
