import React, { useMemo, useState } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import NavBar from './components/NavBar'
import OffersPage from './pages/OffersPage'
import SubmitRequestPage from './pages/SubmitRequestPage'
import AdminDashboard from './pages/AdminDashboard'

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
          mode: 'light',
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
      : {}

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
      <BrowserRouter>
        <NavBar onToggleContrast={toggleContrast} onCycleFontSize={cycleFontSize} />
        <Routes>
          <Route path="/" element={<OffersPage />} />
          <Route path="/anfrage" element={<SubmitRequestPage />} />
          <Route path="/admin" element={<AdminDashboard />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  )
}

export default App
