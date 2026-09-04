import { createContext, useContext, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';

const DarkModeContext = createContext();

export function DarkModeProvider({ children }) {
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem('smre-dark-mode') === 'true';
  });
  const location = useLocation();

  useEffect(() => {
    const root = document.documentElement;
    const isLogin = location.pathname === '/login' || location.pathname === '/';
    if (darkMode && !isLogin) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
    localStorage.setItem('smre-dark-mode', darkMode);
  }, [darkMode, location.pathname]);

  return (
    <DarkModeContext.Provider value={{ darkMode, setDarkMode }}>
      {children}
    </DarkModeContext.Provider>
  );
}

export function useDarkMode() {
  return useContext(DarkModeContext);
}
