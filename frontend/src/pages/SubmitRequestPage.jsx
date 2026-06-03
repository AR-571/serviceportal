import React, { useEffect, useState } from 'react'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'
import Box from '@mui/material/Box'
import Alert from '@mui/material/Alert'
import { useAuth } from '../context/AuthContext'

export default function SubmitRequestPage() {
  const auth = useAuth()
  const [offers, setOffers] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [serviceOfferId, setServiceOfferId] = useState('')
  const [statusMsg, setStatusMsg] = useState(null)

  useEffect(() => {
    let mounted = true
    fetch('http://localhost:8080/api/offers')
      .then((r) => r.json())
      .then((d) => mounted && setOffers(d || []))
      .finally(() => mounted && setLoading(false))
    return () => (mounted = false)
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setStatusMsg(null)
    const payload = {
      message,
      status: 'OPEN',
      requesterId: auth.user?.id,
      serviceOfferId: Number(serviceOfferId),
    }

    try {
      const res = await fetch('http://localhost:8080/api/requests', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Basic ' + auth.credentials,
        },
        body: JSON.stringify(payload),
      })
      if (!res.ok) throw new Error('Failed')
      setMessage('')
      setServiceOfferId('')
      setStatusMsg({ type: 'success', text: 'Anfrage erfolgreich erstellt.' })
    } catch (err) {
      setStatusMsg({ type: 'error', text: 'Fehler beim Erstellen der Anfrage.' })
    }
  }

  return (
    <Container maxWidth="sm" sx={{ py: 4 }}>
      <Typography variant="h5" component="h1" gutterBottom>
        Anfrage stellen
      </Typography>

      {statusMsg && (
        <Alert severity={statusMsg.type} sx={{ mb: 2 }}>
          {statusMsg.text}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <TextField
          label="Nachricht"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          required
          fullWidth
          multiline
          minRows={3}
          sx={{ mb: 2 }}
        />

        <TextField
          select
          label="Angebot wählen"
          value={serviceOfferId}
          onChange={(e) => setServiceOfferId(e.target.value)}
          required
          fullWidth
          sx={{ mb: 2 }}
        >
          {loading ? (
            <MenuItem value="">Lade...</MenuItem>
          ) : offers.length === 0 ? (
            <MenuItem value="">Keine Angebote</MenuItem>
          ) : (
            offers.map((o) => (
              <MenuItem key={o.id} value={o.id}>
                {o.title}
              </MenuItem>
            ))
          )}
        </TextField>

        <Button type="submit" variant="contained">
          Absenden
        </Button>
      </Box>
    </Container>
  )
}
