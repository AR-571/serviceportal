import React from 'react'
import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import Box from '@mui/material/Box'
import { useAuth } from '../context/AuthContext'

export default function NavBar({ onToggleContrast, onCycleFontSize }) {
  const auth = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    auth.logout()
    navigate('/login')
  }

  return (
    <AppBar position="static" sx={{ bgcolor: 'background.paper' }}>
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1, color: 'text.primary', fontWeight: 'bold' }}>
          Serviceportal
        </Typography>

        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          {auth.isLoggedIn ? (
            <>
              <Button color="primary" component={RouterLink} to="/">
                Angebote
              </Button>
              <Button color="primary" component={RouterLink} to="/anfrage">
                Anfrage stellen
              </Button>
              {auth.isAdmin && (
                <Button color="primary" component={RouterLink} to="/admin">
                  Admin
                </Button>
              )}
              <Button color="primary" onClick={auth.logout}>
                Logout
              </Button>
            </>
          ) : (
            <Button color="primary" component={RouterLink} to="/login">
              Login
            </Button>
          )}

          <Button color="primary" onClick={onToggleContrast} aria-pressed="false">
            Kontrast umschalten
          </Button>
          <Button color="primary" onClick={onCycleFontSize}>
            Schriftgröße ändern
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  )
}
