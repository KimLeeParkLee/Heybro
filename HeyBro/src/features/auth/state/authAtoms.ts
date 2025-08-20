import { atom } from 'jotai';
import { OAuthNewUserData } from '../types/auth.types';

// API 응답으로 오는 사용자 정보 타입
export interface User {
  user_id: number;
  nickname: string;
  gender: 'male' | 'female' | 'other';
  birth_date: string;
  notification_enabled: boolean;
  bro_point: number;
  bro_level: number;
  user_type: string;
  onboarding_completed: boolean; // Add this line
}

// 인증 세션 정보 타입
export interface Session {
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
}

// --- Atom Definitions ---

// 사용자 프로필 정보를 저장하는 Atom
// 초기값은 null이며, 로그인 시 실제 데이터로 채워집니다.
export const userAtom = atom<User | null>(null);

// 인증 상태 및 토큰을 저장하는 Atom
// 앱의 초기 상태는 로그아웃 상태입니다.
export const sessionAtom = atom<Session>({
  isAuthenticated: false,
  accessToken: null,
  refreshToken: null,
});

// OAuth로 로그인했지만 아직 회원가입이 완료되지 않은 신규 사용자 정보를 임시 저장하는 Atom
export const newOAuthUserAtom = atom<OAuthNewUserData | null>(null);
