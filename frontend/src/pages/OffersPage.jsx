import React, { useEffect, useState } from 'react'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CircularProgress from '@mui/material/CircularProgress'
import Grid from '@mui/material/Grid'
import Box from '@mui/material/Box'

export default function OffersPage() {
  const [offers, setOffers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    fetch('http://localhost:8080/api/offers')
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
  }, [])

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Barrierefreies Serviceportal - Angebote
      </Typography>

      {loading ? (
        <Box role="status" aria-live="polite" sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <CircularProgress aria-hidden="true" />
          <Typography>Laden...</Typography>
        </Box>
      ) : (
        <Grid container spacing={2}>
          {offers.length === 0 ? (
            <Grid item xs={12}>
              <Typography>Keine Angebote gefunden.</Typography>
            </Grid>
          ) : (
            offers.map((offer) => (
              <Grid item xs={12} sm={6} key={offer.id}>
                <Card role="article" aria-label={offer.title}>
                  <CardContent>
                    <Typography variant="h6" component="h2">
                      {offer.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {offer.description}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))
          )}
        </Grid>
      )}
    </Container>
  )
}
