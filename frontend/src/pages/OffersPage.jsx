import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CardActionArea from '@mui/material/CardActionArea'
import CircularProgress from '@mui/material/CircularProgress'
import Grid from '@mui/material/Grid'
import Box from '@mui/material/Box'

export default function OffersPage() {
  const navigate = useNavigate()
  const auth = useAuth()
  const [offers, setOffers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    const headers = {}
    if (auth.credentials) {
      headers['Authorization'] = 'Basic ' + auth.credentials
    }
    fetch('http://localhost:8080/api/offers', { headers })
      .then((res) => {
        if (!res.ok) throw new Error('Network response was not ok')
        return res.json()
      })
      .then((data) => mounted && setOffers(data || []))
      .catch(() => mounted && setOffers([]))
      .finally(() => mounted && setLoading(false))

    return () => {
      mounted = false
    }
  }, [auth.credentials])

  return (
    <Container maxWidth="md" sx={{ py: 4, bgcolor: 'background.default', minHeight: '100vh' }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'text.primary', fontWeight: 'bold' }}>
        Barrierefreies Serviceportal - Angebote
      </Typography>

      {loading ? (
        <Box role="status" aria-live="polite" sx={{ display: 'flex', alignItems: 'center', gap: 2, color: 'text.primary' }}>
          <CircularProgress aria-hidden="true" sx={{ color: 'primary.main' }} />
          <Typography sx={{ color: 'text.primary' }}>Laden...</Typography>
        </Box>
      ) : (
        <Grid container spacing={2}>
          {offers.length === 0 ? (
            <Grid item xs={12}>
              <Typography sx={{ color: 'text.primary' }}>Keine Angebote gefunden.</Typography>
            </Grid>
          ) : (
            offers.map((offer) => (
              <Grid item xs={12} sm={6} key={offer.id}>
                <Card sx={{ bgcolor: 'background.paper', '&:hover': { bgcolor: 'rgba(124, 58, 237, 0.08)' } }}>
                  <CardActionArea onClick={() => navigate(`/angebote/${offer.id}`)}>
                    <CardContent>
                      <Typography variant="h6" component="h2" sx={{ color: 'text.primary', fontWeight: 600 }}>
                        {offer.title}
                      </Typography>
                      <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                        {offer.description.length > 100 ? offer.description.substring(0, 100) + '...' : offer.description}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              </Grid>
            ))
          )}
        </Grid>
      )}
    </Container>
  )
}
