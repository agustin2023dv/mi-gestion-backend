export type AuthView = 'login' | 'register' | 'forgot';

export interface AuthMockupProps {
  onLogin: () => void;
}

export interface AuthFormProps {
  setView: (v: AuthView) => void;
  onLogin?: () => void;
}
