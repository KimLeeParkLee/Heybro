import { useAtom } from 'jotai';
import EncryptedStorage from 'react-native-encrypted-storage';
import { sessionAtom, userAtom, User, Session } from '../state/authAtoms';

// API 로그인 응답 타입 정의
interface LoginResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
}

// 보안 저장소에 사용될 키 정의
const SESSION_STORAGE_KEY = 'heybro_session';

export const useAuth = () => {
  const [session, setSession] = useAtom(sessionAtom);
  const [, setUser] = useAtom(userAtom);

  // 로그인 처리 함수
  const login = async (response: LoginResponse) => {
    const newSession: Session = {
      isAuthenticated: true,
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
    };

    // 1. Jotai 상태 업데이트
    setSession(newSession);
    setUser(response.user);

    // 2. 보안 저장소에 세션 정보 저장
    await EncryptedStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(newSession));
  };

  // 로그아웃 처리 함수
  const logout = async () => {
    // 1. Jotai 상태 초기화
    setSession({ isAuthenticated: false, accessToken: null, refreshToken: null });
    setUser(null);

    // 2. 보안 저장소에서 세션 정보 삭제
    await EncryptedStorage.removeItem(SESSION_STORAGE_KEY);
  };

  // 앱 시작 시 세션 복원 함수
  const restoreSession = async () => {
    try {
      const storedSession = await EncryptedStorage.getItem(SESSION_STORAGE_KEY);
      if (storedSession) {
        const parsedSession: Session = JSON.parse(storedSession);
        // TODO: 여기서 refreshToken 유효성 검사 후 accessToken 재발급 로직 추가 가능
        setSession(parsedSession);
      }
    } catch (error) {
      console.error("Failed to restore session:", error);
      // 세션 복원 실패 시 로그아웃 처리
      await logout();
    }
  };

  return {
    ...session,
    login,
    logout,
    restoreSession,
  };
};
