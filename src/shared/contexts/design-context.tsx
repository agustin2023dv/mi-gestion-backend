import React, { createContext, useContext, useState, useEffect } from 'react';

export interface DesignSettings {
  primaryColor: string;
  backgroundColor: string;
  fontFamily: 'sans' | 'serif' | 'mono';
  gridColumns: 1 | 2 | 3;
  cardStyle: 'minimal' | 'glass' | 'bordered';
  showCategories: boolean;
  accentColor: string;
}

const DEFAULT_SETTINGS: DesignSettings = {
  primaryColor: '#1c1917',
  backgroundColor: '#faf9f6',
  fontFamily: 'serif',
  gridColumns: 3,
  cardStyle: 'minimal',
  showCategories: true,
  accentColor: '#8b5cf6'
};

interface DesignContextType {
  settings: DesignSettings;
  updateSettings: (newSettings: Partial<DesignSettings>) => void;
  resetSettings: () => void;
}

const DesignContext = createContext<DesignContextType | undefined>(undefined);

export function DesignProvider({ children }: { children: React.ReactNode }) {
  const [settings, setSettings] = useState<DesignSettings>(() => {
    const saved = localStorage.getItem('tenant_design_settings');
    return saved ? JSON.parse(saved) : DEFAULT_SETTINGS;
  });

  useEffect(() => {
    localStorage.setItem('tenant_design_settings', JSON.stringify(settings));
  }, [settings]);

  const updateSettings = (newSettings: Partial<DesignSettings>) => {
    setSettings(prev => ({ ...prev, ...newSettings }));
  };

  const resetSettings = () => {
    setSettings(DEFAULT_SETTINGS);
  };

  return (
    <DesignContext.Provider value={{ settings, updateSettings, resetSettings }}>
      {children}
    </DesignContext.Provider>
  );
}

export function useDesign() {
  const context = useContext(DesignContext);
  if (context === undefined) {
    throw new Error('useDesign must be used within a DesignProvider');
  }
  return context;
}
