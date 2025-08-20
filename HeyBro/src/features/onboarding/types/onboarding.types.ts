export interface SurveyOption {
  survey_option_id: number;
  option_content: string;
  display_order: number;
}

export interface SurveyQuestion {
  survey_question_id: number;
  survey_question_content: string;
  display_order: number;
  options: SurveyOption[];
}

export interface OnboardingAnswer {
  survey_question_id: number;
  survey_option_id: number;
}

export interface SubmitAnswersRequest {
  answers: OnboardingAnswer[];
}

export interface SubmitAnswersResponse {
  user_type: string;
}
