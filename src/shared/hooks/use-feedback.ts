import { useState, useCallback } from 'react';

export function useFeedback(duration = 3000) {
  const [isActive, setIsActive] = useState(false);

  const trigger = useCallback(() => {
    setIsActive(true);
    const timer = setTimeout(() => {
      setIsActive(false);
    }, duration);

    return () => clearTimeout(timer);
  }, [duration]);

  return {
    isActive,
    trigger
  };
}
