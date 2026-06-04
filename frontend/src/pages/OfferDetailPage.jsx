import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CircularProgress from '@mui/material/CircularProgress'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import { useAuth } from '../context/AuthContext'

export default function OfferDetailPage() {
  const { id } = useParams()
  const auth = useAuth()
  const [offer, setOffer] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    const headers = {}
    if (auth.credentials) {
      headers['Authorization'] = 'Basic ' + auth.credentials
    }
    fetch(`http://localhost:8080/api/offers/${id}`, { headers })
      .then((res) => {
        if (!res.ok) throw new Error('Network response was not ok')
        return res.json()
      })
      .then((data) => mounted && setOffer(data))
      .catch(() => mounted && setOffer(null))
      .finally(() => mounted && setLoading(false))

    return () => {
      mounted = false
    }
  }, [id, auth.credentials])

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ py: 4, bgcolor: 'background.default', minHeight: '100vh' }}>
        <Box role="status" aria-live="polite" sx={{ display: 'flex', alignItems: 'center', gap: 2, color: 'text.primary' }}>
          <CircularProgress aria-hidden="true" sx={{ color: 'primary.main' }} />
          <Typography sx={{ color: 'text.primary' }}>Laden...</Typography>
        </Box>
      </Container>
    )
  }

  if (!offer) {
    return (
      <Container maxWidth="md" sx={{ py: 4, bgcolor: 'background.default', minHeight: '100vh' }}>
        <Typography sx={{ color: 'text.primary' }}>Angebot nicht gefunden.</Typography>
      </Container>
    )
  }

  return (
    <Container maxWidth="md" sx={{ py: 4, bgcolor: 'background.default', minHeight: '100vh' }}>
      <Button
        onClick={() => window.history.back()}
        sx={{ mb: 2, color: 'text.secondary' }}
      >
        ← Zurück
      </Button>
      
      <Card sx={{ bgcolor: 'background.paper' }}>
        <CardContent>
          <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'text.primary', fontWeight: 'bold' }}>
            {offer.title}
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary', whiteSpace: 'pre-wrap', mt: 2 }}>
            {offer.description}
          </Typography>
        </CardContent>
      </Card>
    </Container>
  )
}
