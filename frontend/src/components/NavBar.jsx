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
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Serviceportal
        </Typography>

        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          {auth.isLoggedIn ? (
            <>
              <Button color="inherit" component={RouterLink} to="/">
                Angebote
              </Button>
              <Button color="inherit" component={RouterLink} to="/anfrage">
                Anfrage stellen
              </Button>
              {auth.isAdmin && (
                <Button color="inherit" component={RouterLink} to="/admin">
                  Admin
                </Button>
              )}
              <Button color="inherit" onClick={auth.logout}>
                Logout
              </Button>
            </>
          ) : (
            <Button color="inherit" component={RouterLink} to="/login">
              Login
            </Button>
          )}

          <Button color="inherit" onClick={onToggleContrast} aria-pressed="false">
            Kontrast umschalten
          </Button>
          <Button color="inherit" onClick={onCycleFontSize}>
            Schriftgröße ändern
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  )
}
