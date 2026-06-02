import React from 'react'
import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import MenuIcon from '@mui/icons-material/Menu'
import { Link as RouterLink } from 'react-router-dom'
import Box from '@mui/material/Box'

export default function NavBar({ onToggleContrast, onCycleFontSize }) {
  return (
    <AppBar position="static">
      <Toolbar>
        <IconButton edge="start" color="inherit" aria-label="menu" sx={{ mr: 2 }}>
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Serviceportal
        </Typography>

        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <Button color="inherit" component={RouterLink} to="/">
            Angebote
          </Button>
          <Button color="inherit" component={RouterLink} to="/anfrage">
            Anfrage stellen
          </Button>
          <Button color="inherit" component={RouterLink} to="/admin">
            Admin
          </Button>

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
