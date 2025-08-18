import { User } from '../state/authAtoms';

// --- Standard Auth ---

/**
 * 로그인 또는 회원가입 성공 시 서버로부터 받는 `data` 객체의 타입입니다.
 */
export interface AuthResponseData extends User {
  access_token: string;
  refresh_token: string;
}

/**
 * 이메일 로그인 API 요청 시 `body`에 포함될 데이터 타입입니다.
 */
export interface LoginCredentials {
  email: string;
  password: string;
}

/**
 * 회원가입 API 요청 시 `body`에 포함될 데이터 타입입니다.
 * OAuth 가입 시에는 비밀번호가 없으므로 optional 처리하고,
 * provider와 oauth_token을 추가하여 서버가 구분할 수 있도록 합니다.
 */
export interface RegisterCredentials {
  user_name: string;
  nickname: string;
  email: string;
  password?: string; // OAuth 가입 시에는 없음
  gender: 'male' | 'female' | 'other';
  birth_date: string;
  phone: string;
  privacy_consent: boolean;
  marketing_consent: boolean;
  notification_enabled: boolean;
  provider?: 'google' | 'kakao'; // OAuth 가입 시에만 포함
  oauth_token?: string; // OAuth 가입 시에만 포함
}

// --- OAuth ---

/**
 * OAuth 로그인 시도 시 우리 서버로 보내는 요청 `body`의 타입입니다.
 */
export interface OAuthLoginRequest {
  provider: 'google' | 'kakao';
  oauth_token: string;
}

/**
 * OAuth 시도 시 신규 사용자일 경우 서버가 반환하는 데이터 타입입니다.
 * 이 데이터를 회원가입 화면으로 전달하여 추가 정보를 입력받아야 합니다.
 */
export interface OAuthNewUserData {
  is_new_user: true;
  email: string;
  provider: 'google' | 'kakao';
  oauth_token: string; // 최종 회원가입을 위해 이 토큰을 다시 사용해야 함
}

/**
 * OAuth 시도 시 서버의 최종 응답 타입입니다.
 * 기존 유저라면 바로 로그인 처리되고, 신규 유저라면 추가 정보 입력을 위한 데이터를 받습니다.
 */
export type OAuthLoginResponse = AuthResponseData | OAuthNewUserData;
