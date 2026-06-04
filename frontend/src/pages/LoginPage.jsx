import React, { useState } from 'react'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Box from '@mui/material/Box'
import Alert from '@mui/material/Alert'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const auth = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    const success = await auth.login(username, password)
    setLoading(false)

    if (success) {
      navigate('/')
    } else {
      setError('Login fehlgeschlagen. Überprüfe Benutzername und Passwort.')
    }
  }

  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'text.primary', fontWeight: 'bold' }}>
        Login
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} noValidate sx={{ bgcolor: 'background.paper', p: 4, borderRadius: 2 }}>
        <TextField
          label="Benutzername"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          fullWidth
          sx={{ 
            mb: 2,
            '& .MuiInputBase-root': { color: 'text.primary' },
            '& .MuiInputLabel-root': { color: 'text.secondary' },
          }}
        />

        <TextField
          label="Passwort"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          fullWidth
          sx={{ 
            mb: 3,
            '& .MuiInputBase-root': { color: 'text.primary' },
            '& .MuiInputLabel-root': { color: 'text.secondary' },
          }}
        />

        <Button
          type="submit"
          variant="contained"
          fullWidth
          disabled={loading}
          color="primary"
        >
          {loading ? 'Anmelden...' : 'Anmelden'}
        </Button>
      </Box>

      <Box sx={{ mt: 4, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
        <Typography variant="body2" sx={{ mb: 1, color: 'text.primary' }}>
          <strong>Testdaten:</strong>
        </Typography>
        <Typography variant="caption" sx={{ color: 'text.secondary' }}>
          Benutzer: testuser / Passwort: password123
        </Typography>
        <br />
        <Typography variant="caption" sx={{ color: 'text.secondary' }}>
          Admin: admin / Passwort: admin123
        </Typography>
      </Box>
    </Container>
  )
}
