import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  Alert,
  TouchableOpacity,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { BottomButton } from '../../../shared/components/ui/BottomButton';
import { useAuth } from '../../auth/hooks/useAuth';
import { getOnboardingQuestions, submitOnboardingAnswers } from '../services/onboardingService';
import { SurveyQuestion } from '../types/onboarding.types';
import { HttpError } from '../../../shared/api/http';
import { AppStackParamList } from '../../../app/navigation/RootNavigator';

type OnboardingScreenNavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'Onboarding'
>;

const OnboardingScreen = () => {
  const navigation = useNavigation<OnboardingScreenNavigationProp>();
  const { updateUser } = useAuth();

  const [testStarted, setTestStarted] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [testCompleted, setTestCompleted] = useState(false);
  const [questions, setQuestions] = useState<SurveyQuestion[]>([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<Record<number, number>>({});
  const [userType, setUserType] = useState('');

  const handleStartTest = async () => {
    try {
      setIsLoading(true);
      const fetchedQuestions = await getOnboardingQuestions();
      const sortedQuestions = fetchedQuestions
        .sort((a, b) => a.display_order - b.display_order)
        .map(q => ({
          ...q,
          options: q.options.sort((a, b) => a.display_order - b.display_order),
        }));
      setQuestions(sortedQuestions);
      setTestStarted(true);
    } catch (error) {
      const errorMessage =
        error instanceof HttpError ? error.message : '질문을 불러오는 데 실패했습니다.';
      Alert.alert('오류', errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectOption = (questionId: number, optionId: number) => {
    setAnswers(prev => ({ ...prev, [questionId]: optionId }));
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1);
    }
  };

  const handlePrevious = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(prev => prev - 1);
    }
  };

  const handleNext = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1);
    }
  };

  const handleSubmit = async () => {
    setIsLoading(true);
    try {
      const formattedAnswers = {
        answers: Object.entries(answers).map(([questionId, optionId]) => ({
          survey_question_id: Number(questionId),
          survey_option_id: optionId,
        })),
      };
      
      const response = await submitOnboardingAnswers(formattedAnswers);
      
      updateUser({ user_type: response.user_type });
      setUserType(response.user_type); // Store for navigation
      setTestCompleted(true); // Show the completion screen
      
    } catch (error) {
      const errorMessage =
        error instanceof HttpError ? error.message : '답변 제출에 실패했습니다.';
      Alert.alert('오류', errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNavigateToResult = () => {
    navigation.navigate('OnboardingResult', { user_type: userType });
  };

  // Screen: Initial
  if (!testStarted) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.centered}>
          <Text style={styles.introText}>
            지금 브로 유형테스트를 받아보고, 헤이브로 루틴을 시작하세요!
          </Text>
        </View>
        <BottomButton
          title={isLoading ? '준비 중...' : '테스트 시작하기'}
          onPress={handleStartTest}
          disabled={isLoading}
        />
      </SafeAreaView>
    );
  }

  // Screen: Test Completed
  if (testCompleted) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.centered}>
          <Text style={styles.introText}>브로 유형 테스트 완료!</Text>
        </View>
        <BottomButton
          title="나의 유형 확인하기"
          onPress={handleNavigateToResult}
        />
      </SafeAreaView>
    );
  }

  // Screen: Loading Questions
  if (isLoading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" />
        <Text>질문을 불러오는 중...</Text>
      </View>
    );
  }

  if (questions.length === 0 && !isLoading) {
    return (
      <View style={styles.centered}>
        <Text>표시할 질문이 없습니다.</Text>
      </View>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const isLastQuestion = currentQuestionIndex === questions.length - 1;
  const allQuestionsAnswered = Object.keys(answers).length === questions.length;

  // Screen: Main Test UI
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>{currentQuestion.survey_question_content}</Text>
        <View style={styles.quadrantContainer}>
          {currentQuestion.options.map(option => (
            <TouchableOpacity
              key={option.survey_option_id}
              style={[
                styles.quadrant,
                answers[currentQuestion.survey_question_id] === option.survey_option_id &&
                  styles.selectedOption,
              ]}
              onPress={() =>
                handleSelectOption(currentQuestion.survey_question_id, option.survey_option_id)
              }>
              <Text style={styles.optionText}>{option.option_content}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {isLastQuestion && allQuestionsAnswered ? (
        <BottomButton title="설문 완료" onPress={handleSubmit} disabled={isLoading} />
      ) : (
        <View style={styles.navigationButtons}>
          <TouchableOpacity
            onPress={handlePrevious}
            disabled={currentQuestionIndex === 0}
            style={[styles.navButton, currentQuestionIndex === 0 && styles.disabledButton]}>
            <Text>이전</Text>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={handleNext}
            disabled={isLastQuestion || !answers[currentQuestion.survey_question_id]}
            style={[
              styles.navButton,
              (isLastQuestion || !answers[currentQuestion.survey_question_id]) &&
                styles.disabledButton,
            ]}>
            <Text>다음</Text>
          </TouchableOpacity>
        </View>
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: 'white' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  introText: { fontSize: 18, textAlign: 'center', lineHeight: 26 },
  content: { flex: 1, justifyContent: 'center', padding: 16 },
  title: { fontSize: 22, fontWeight: 'bold', textAlign: 'center', marginBottom: 40 },
  quadrantContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  quadrant: {
    width: '48%',
    height: 150,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
    borderRadius: 8,
    marginBottom: 16,
    padding: 10,
  },
  selectedOption: {
    backgroundColor: '#3B82F6',
    borderColor: '#2563EB',
    borderWidth: 2,
  },
  optionText: { fontSize: 16, textAlign: 'center' },
  navigationButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 16,
  },
  navButton: {
    backgroundColor: '#E5E7EB',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
  },
  disabledButton: { backgroundColor: '#D1D5DB', opacity: 0.6 },
});

export default OnboardingScreen;
