import React, { useEffect, useState } from 'react'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import CircularProgress from '@mui/material/CircularProgress'
import Box from '@mui/material/Box'
import { useAuth } from '../context/AuthContext'

export default function AdminDashboard() {
  const auth = useAuth()
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    fetch('http://localhost:8080/api/requests', {
      headers: {
        'Authorization': 'Basic ' + auth.credentials,
      },
    })
      .then((r) => r.json())
      .then((d) => mounted && setRequests(d || []))
      .finally(() => mounted && setLoading(false))
    return () => (mounted = false)
  }, [auth.credentials])

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h5" gutterBottom>
        Admin Dashboard - Anfragen
      </Typography>

      {loading ? (
        <Box role="status" aria-live="polite" sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <CircularProgress aria-hidden="true" />
          <Typography>Laden...</Typography>
        </Box>
      ) : (
        <TableContainer component={Paper}>
          <Table aria-label="requests table">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Nachricht</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Requester ID</TableCell>
                <TableCell>ServiceOffer ID</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {requests.map((r) => (
                <TableRow key={r.id}>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>{r.message}</TableCell>
                  <TableCell>{r.status}</TableCell>
                  <TableCell>{r.requester ? r.requester.id : '—'}</TableCell>
                  <TableCell>{r.serviceOffer ? r.serviceOffer.id : '—'}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  )
}
