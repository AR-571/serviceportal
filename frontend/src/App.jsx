import React, { useMemo, useState } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import { AuthProvider, useAuth } from './context/AuthContext'
import NavBar from './components/NavBar'
import LoginPage from './pages/LoginPage'
import OffersPage from './pages/OffersPage'
import OfferDetailPage from './pages/OfferDetailPage'
import SubmitRequestPage from './pages/SubmitRequestPage'
import AdminDashboard from './pages/AdminDashboard'

function ProtectedRoute({ children }) {
  const auth = useAuth()
  if (!auth.isLoggedIn) return <Navigate to="/login" replace />
  return children
}

function AdminRoute({ children }) {
  const auth = useAuth()
  if (!auth.isLoggedIn) return <Navigate to="/login" replace />
  if (!auth.isAdmin) return <Navigate to="/" replace />
  return children
}
function App() {
  const [isHighContrast, setIsHighContrast] = useState(false)
  const [fontSize, setFontSize] = useState('medium') // 'small' | 'medium' | 'large'

  const toggleContrast = () => setIsHighContrast((v) => !v)
  const cycleFontSize = () => setFontSize((s) => (s === 'small' ? 'medium' : s === 'medium' ? 'large' : 'small'))

  const theme = useMemo(() => {
    const baseTypography = {
      small: { fontSize: 13 },
      medium: { fontSize: 16 },
      large: { fontSize: 20 },
    }

    const palette = isHighContrast
      ? {
          mode: 'light',
          primary: { main: '#000000' },
          background: { default: '#ffffff', paper: '#ffffff' },
          text: { primary: '#000000', secondary: '#000000' },
        }
      : {
          mode: 'dark',
          primary: {
            main: '#7c3aed', // modern purple
          },
          secondary: {
            main: '#06b6d4', // cyan
          },
          background: {
            default: '#0f172a', // dark slate
            paper: '#1e293b', // lighter slate
          },
          text: {
            primary: '#f1f5f9', // light gray
            secondary: '#94a3b8', // medium gray
          },
        }

    // high-contrast accent option (yellow on black) for components
    const overrides = isHighContrast
      ? {
          components: {
            MuiButton: {
              styleOverrides: {
                root: { backgroundColor: '#000000', color: '#ffff00' },
              },
            },
          },
        }
      : {
          components: {
            MuiTableCell: {
              styleOverrides: {
                root: {
                  color: '#f1f5f9',
                },
                head: {
                  backgroundColor: '#334155',
                  color: '#f1f5f9',
                  fontWeight: 600,
                },
              },
            },
            MuiPaper: {
              styleOverrides: {
                root: {
                  backgroundColor: '#1e293b',
                },
              },
            },
          },
        }

    return createTheme({
      palette,
      typography: {
        htmlFontSize: baseTypography[fontSize].fontSize,
      },
      ...overrides,
    })
  }, [isHighContrast, fontSize])

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <BrowserRouter>
          <NavBar onToggleContrast={toggleContrast} onCycleFontSize={cycleFontSize} />
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/" element={<OffersPage />} />
            <Route path="/angebote/:id" element={<OfferDetailPage />} />
            <Route
              path="/anfrage"
              element={
                <ProtectedRoute>
                  <SubmitRequestPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/admin"
              element={
                <AdminRoute>
                  <AdminDashboard />
                </AdminRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  )
}

export default App
