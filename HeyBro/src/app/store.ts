import { createStore } from 'jotai';

/**
 * 앱 전역에서 사용될 중앙 Jotai 스토어입니다.
 * 이 스토어를 사용하면 React 컴포넌트 외부에서도
 * 상태를 안전하게 읽거나 업데이트할 수 있습니다.
 */
export const jotaiStore = createStore();
