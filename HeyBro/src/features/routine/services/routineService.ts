import { httpRequest } from '../../../shared/api/http';
import { UserRoutine, MonthlyAchievement, RoutineSubElement } from '../types/routine.types';

type Weekday =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface GetUserRoutinesParams {
  period_type: 'day' | 'week'; // 'month' is handled by the new function
  date?: string; // YYYY-MM-DD
  week?: string; // YYYY-WW
  weekday?: Weekday;
}

export const getUserRoutines = async (
  params: GetUserRoutinesParams,
): Promise<UserRoutine[]> => {
  const queryParams = new URLSearchParams();
  for (const key in params) {
    if (Object.prototype.hasOwnProperty.call(params, key)) {
      const value = params[key as keyof GetUserRoutinesParams];
      if (value !== undefined) {
        queryParams.append(key, value);
      }
    }
  }
  const endpoint = `/api/routine/user?${queryParams.toString()}`;
  return httpRequest<UserRoutine[]>(endpoint, { method: 'GET' });
};

export const getMonthlyAchievements = async (
  month: string, // YYYY-MM
): Promise<MonthlyAchievement[]> => {
  const endpoint = `/api/routine/user?period_type=month&month=${month}`;
  return httpRequest<MonthlyAchievement[]>(endpoint, { method: 'GET' });
};

export const getRoutineElementDetail = async (
  routineElementId: number,
): Promise<RoutineSubElement[]> => {
  const endpoint = `/api/routine/elements/${routineElementId}/detail`;
  return httpRequest<RoutineSubElement[]>(endpoint, { method: 'GET' });
};
