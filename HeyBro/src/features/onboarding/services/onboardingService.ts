import { httpRequest } from '../../../shared/api/http';
import {
  SurveyQuestion,
  SubmitAnswersRequest,
  SubmitAnswersResponse,
} from '../types/onboarding.types';

export const getOnboardingQuestions = async (): Promise<SurveyQuestion[]> => {
  return httpRequest<SurveyQuestion[]>('/api/users/onboarding-test', {
    method: 'GET',
  });
};

export const submitOnboardingAnswers = async (
  answers: SubmitAnswersRequest,
): Promise<SubmitAnswersResponse> => {
  return httpRequest<SubmitAnswersResponse>('/api/users/onboarding-test', {
    method: 'POST',
    body: JSON.stringify(answers),
  });
};
