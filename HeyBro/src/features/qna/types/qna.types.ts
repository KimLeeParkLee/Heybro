export interface Question {
  question_id: number;
  title: string;
  created_at: string; // ISO 8601 format date string
  views: number;
  answer_count: number;
  question_thumbnail_image_url: string;
}
