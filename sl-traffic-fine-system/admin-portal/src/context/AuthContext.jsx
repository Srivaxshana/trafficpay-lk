import { createContext, useContext, useState } from 'react'
import { setToken, clearToken } from '../api/adminApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)

  const loginUser = (token, userInfo) => {
    setToken(token)
    setUser(userInfo)
  }

  const logoutUser = () => {
    clearToken()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loginUser, logoutUser, isLoggedIn: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
