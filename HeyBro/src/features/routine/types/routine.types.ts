export interface RoutineElement {
  user_routine_element_id: number;
  time: string;
  routine_element_id: number;
  routine_element_name: string;
  execution_time: 'MORNING' | 'EVENING' | string;
  is_completed: boolean;
}

export interface UserRoutine {
  weekday: string;
  user_routine_id: number;
  routine_name: string;
  date: string;
  elements: RoutineElement[];
}

export interface MonthlyAchievement {
  date: string; // "YYYY-MM-DD"
  user_routine_id: number;
  completed_rate: number;
}

export interface RoutineTip {
  routine_element_tip_id: number;
  tip_content: string;
}

export interface RoutineSubElement {
  routine_sub_element_id: number;
  routine_sub_element_name: string;
  routine_content: string;
  routine_detail_image: string;
  step: number;
  tips: RoutineTip[];
}
