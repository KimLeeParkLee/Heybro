import { httpRequest } from '../../../shared/api/http';
import { Question } from '../types/qna.types';

export interface GetQuestionsParams {
  sort?: 'view_desc' | 'latest_desc';
  tag?: string;
  page?: number;
  number?: number;
}

export const getQuestions = async (
  params: GetQuestionsParams,
): Promise<Question[]> => {
  const queryParams = new URLSearchParams();

  for (const key in params) {
    if (Object.prototype.hasOwnProperty.call(params, key)) {
      const value = params[key as keyof GetQuestionsParams];
      if (value !== undefined) {
        queryParams.append(key, String(value));
      }
    }
  }

  const endpoint = `/api/question?${queryParams.toString()}`;
  return httpRequest<Question[]>(endpoint, { method: 'GET' });
};
