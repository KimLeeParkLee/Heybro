import React from 'react';
import { View, Text, SafeAreaView, StyleSheet } from 'react-native';
import { useRoute, RouteProp } from '@react-navigation/native';
import { BottomButton } from '../../../shared/components/ui/BottomButton';
import { useAuth } from '../../auth/hooks/useAuth';
import { AppStackParamList } from '../../../app/navigation/RootNavigator';

type OnboardingResultScreenRouteProp = RouteProp<
  AppStackParamList,
  'OnboardingResult'
>;

const OnboardingResultScreen = () => {
  const { completeOnboarding } = useAuth();
  const route = useRoute<OnboardingResultScreenRouteProp>();
  const { user_type } = route.params;

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>당신의 브로 유형은?</Text>
        <View style={styles.resultBox}>
          <Text style={styles.resultText}>{user_type}</Text>
        </View>
      </View>
      <BottomButton title="헤이브로 시작하기" onPress={completeOnboarding} />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 32,
    textAlign: 'center',
  },
  resultBox: {
    padding: 24,
    backgroundColor: '#F3F4F6',
    borderRadius: 12,
    minWidth: 200,
    alignItems: 'center',
  },
  resultText: {
    fontSize: 20,
    fontWeight: '600',
    color: '#1F2937',
  },
});

export default OnboardingResultScreen;
