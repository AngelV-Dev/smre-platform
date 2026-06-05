import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => {
    // Intentar recuperar sesión del sessionStorage al recargar
    const token = sessionStorage.getItem('smre_token')
    const user  = sessionStorage.getItem('smre_user')
    return token ? { token, user: JSON.parse(user) } : null
  })

  const login = (token, user) => {
    sessionStorage.setItem('smre_token', token)
    sessionStorage.setItem('smre_user', JSON.stringify(user))
    setAuth({ token, user })
  }

  const logout = () => {
    sessionStorage.removeItem('smre_token')
    sessionStorage.removeItem('smre_user')
    setAuth(null)
  }

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// Hook para consumir el contexto fácilmente
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return context
}