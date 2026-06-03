import React, { createContext, useState } from 'react'

export const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [credentials, setCredentials] = useState(null)

  const login = async (username, password) => {
    const encoded = btoa(username + ':' + password)
    try {
      const res = await fetch('http://localhost:8080/api/users/me', {
        headers: {
          'Authorization': 'Basic ' + encoded,
        },
      })
      if (!res.ok) throw new Error('Login failed')
      const userData = await res.json()
      setUser(userData)
      setCredentials(encoded)
      return true
    } catch (err) {
      console.error('Login error:', err)
      return false
    }
  }

  const logout = () => {
    setUser(null)
    setCredentials(null)
  }

  const value = {
    user,
    credentials,
    login,
    logout,
    isLoggedIn: !!user,
    isAdmin: user?.role === 'ADMIN',
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = React.useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
