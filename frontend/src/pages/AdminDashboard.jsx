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
import Tabs from '@mui/material/Tabs'
import Tab from '@mui/material/Tab'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Select from '@mui/material/Select'
import MenuItem from '@mui/material/MenuItem'
import FormControl from '@mui/material/FormControl'
import InputLabel from '@mui/material/InputLabel'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import { useAuth } from '../context/AuthContext'

function TabPanel({ children, value, index }) {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  )
}

export default function AdminDashboard() {
  const auth = useAuth()
  const [tabValue, setTabValue] = useState(0)
  
  // Requests state
  const [requests, setRequests] = useState([])
  const [requestsLoading, setRequestsLoading] = useState(true)
  
  // Offers state
  const [offers, setOffers] = useState([])
  const [offersLoading, setOffersLoading] = useState(true)
  const [newOffer, setNewOffer] = useState({ title: '', description: '' })
  const [editOffer, setEditOffer] = useState(null)
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  
  // Users state
  const [users, setUsers] = useState([])
  const [usersLoading, setUsersLoading] = useState(true)

  // Fetch requests
  useEffect(() => {
    let mounted = true
    fetch('http://localhost:8080/api/requests', {
      headers: {
        'Authorization': 'Basic ' + auth.credentials,
      },
    })
      .then((r) => r.json())
      .then((d) => mounted && setRequests(d || []))
      .finally(() => mounted && setRequestsLoading(false))
    return () => (mounted = false)
  }, [auth.credentials])

  // Fetch offers
  useEffect(() => {
    let mounted = true
    fetch('http://localhost:8080/api/offers', {
      headers: {
        'Authorization': 'Basic ' + auth.credentials,
      },
    })
      .then((r) => r.json())
      .then((d) => mounted && setOffers(d || []))
      .finally(() => mounted && setOffersLoading(false))
    return () => (mounted = false)
  }, [auth.credentials])

  // Fetch users
  useEffect(() => {
    let mounted = true
    fetch('http://localhost:8080/api/users', {
      headers: {
        'Authorization': 'Basic ' + auth.credentials,
      },
    })
      .then((r) => r.json())
      .then((d) => mounted && setUsers(d || []))
      .finally(() => mounted && setUsersLoading(false))
    return () => (mounted = false)
  }, [auth.credentials])

  // Update request status
  const handleStatusChange = (requestId, newStatus) => {
    fetch(`http://localhost:8080/api/requests/${requestId}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + auth.credentials,
      },
      body: JSON.stringify({ status: newStatus }),
    })
      .then((r) => {
        if (!r.ok) {
          throw new Error('Fehler beim Ändern des Status')
        }
        return r.json()
      })
      .then((updated) => {
        setRequests(requests.map((r) => (r.id === requestId ? updated : r)))
      })
      .catch((error) => {
        console.error('Fehler:', error)
        alert('Fehler beim Ändern des Status')
      })
  }

  // Delete offer
  const handleDeleteOffer = (offerId) => {
    if (!window.confirm('Angebot wirklich löschen?')) return
    
    fetch(`http://localhost:8080/api/offers/${offerId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': 'Basic ' + auth.credentials,
      },
    })
      .then((r) => {
        if (!r.ok) {
          throw new Error('Fehler beim Löschen des Angebots')
        }
        setOffers(offers.filter((o) => o.id !== offerId))
      })
      .catch((error) => {
        console.error('Fehler:', error)
        alert('Fehler beim Löschen des Angebots')
      })
  }

  // Create new offer
  const handleCreateOffer = (e) => {
    e.preventDefault()
    fetch('http://localhost:8080/api/offers', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + auth.credentials,
      },
      body: JSON.stringify(newOffer),
    })
      .then((r) => {
        if (!r.ok) {
          throw new Error('Fehler beim Erstellen des Angebots')
        }
        return r.json()
      })
      .then((created) => {
        setOffers([...offers, created])
        setNewOffer({ title: '', description: '' })
      })
      .catch((error) => {
        console.error('Fehler:', error)
        alert('Fehler beim Erstellen des Angebots')
      })
  }

  // Edit offer
  const handleEditOffer = (offer) => {
    setEditOffer({ ...offer })
    setEditDialogOpen(true)
  }

  // Update offer
  const handleUpdateOffer = (e) => {
    e.preventDefault()
    fetch(`http://localhost:8080/api/offers/${editOffer.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + auth.credentials,
      },
      body: JSON.stringify({ title: editOffer.title, description: editOffer.description }),
    })
      .then((r) => {
        if (!r.ok) {
          throw new Error('Fehler beim Aktualisieren des Angebots')
        }
        return r.json()
      })
      .then((updated) => {
        setOffers(offers.map((o) => (o.id === updated.id ? updated : o)))
        setEditDialogOpen(false)
        setEditOffer(null)
      })
      .catch((error) => {
        console.error('Fehler:', error)
        alert('Fehler beim Aktualisieren des Angebots')
      })
  }

  // Toggle user role
  const handleToggleRole = (userId, currentRole) => {
    const newRole = currentRole === 'ADMIN' ? 'USER' : 'ADMIN'
    
    // Prüfen: letzter Admin darf nicht degradiert werden
    const adminCount = users.filter(u => u.role === 'ADMIN').length
    if (currentRole === 'ADMIN' && adminCount <= 1) {
      alert('Der letzte Admin darf nicht degradiert werden!')
      return
    }
    
    fetch(`http://localhost:8080/api/users/${userId}/role`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + auth.credentials,
      },
      body: JSON.stringify({ role: newRole }),
    })
      .then((r) => {
        if (!r.ok) {
          throw new Error('Fehler beim Ändern der Rolle')
        }
        return r.json()
      })
      .then((updated) => {
        setUsers(users.map((u) => (u.id === userId ? updated : u)))
      })
      .catch((error) => {
        console.error('Fehler:', error)
        alert('Fehler beim Ändern der Rolle')
      })
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4, bgcolor: 'background.default', minHeight: '100vh' }}>
      <Typography variant="h4" gutterBottom sx={{ color: 'text.primary', fontWeight: 'bold', mb: 3 }}>
        Admin Dashboard
      </Typography>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tab label="Anfragen bearbeiten" />
          <Tab label="Angebote verwalten" />
          <Tab label="Benutzerrollen" />
        </Tabs>
      </Paper>

      {/* Tab 1: Anfragen bearbeiten */}
      <TabPanel value={tabValue} index={0}>
        {requestsLoading ? (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, color: 'text.primary' }}>
            <CircularProgress sx={{ color: 'primary.main' }} />
            <Typography sx={{ color: 'text.primary' }}>Laden...</Typography>
          </Box>
        ) : (
          <TableContainer component={Paper} sx={{ bgcolor: 'background.paper' }}>
            <Table aria-label="requests table">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Nachricht</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Requester</TableCell>
                  <TableCell>Angebot</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {requests.map((r) => (
                  <TableRow key={r.id} sx={{ '&:hover': { bgcolor: 'rgba(124, 58, 237, 0.08)' } }}>
                    <TableCell>{r.id}</TableCell>
                    <TableCell>{r.message}</TableCell>
                    <TableCell>
                      <FormControl size="small" sx={{ minWidth: 120, bgcolor: 'background.default' }}>
                        <Select
                          value={r.status}
                          onChange={(e) => handleStatusChange(r.id, e.target.value)}
                          sx={{ color: 'text.primary', bgcolor: 'background.default' }}
                        >
                          <MenuItem value="OPEN">OPEN</MenuItem>
                          <MenuItem value="IN_PROGRESS">IN_PROGRESS</MenuItem>
                          <MenuItem value="CLOSED">CLOSED</MenuItem>
                        </Select>
                      </FormControl>
                    </TableCell>
                    <TableCell>{r.requesterUsername || '—'}</TableCell>
                    <TableCell>{r.serviceOfferTitle || '—'}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </TabPanel>

      {/* Tab 2: Angebote verwalten */}
      <TabPanel value={tabValue} index={1}>
        <Paper sx={{ p: 3, mb: 3, bgcolor: 'background.paper' }}>
          <Box component="form" onSubmit={handleCreateOffer} sx={{ display: 'flex', gap: 2, alignItems: 'flex-end' }}>
            <TextField
              label="Titel"
              value={newOffer.title}
              onChange={(e) => setNewOffer({ ...newOffer, title: e.target.value })}
              required
              sx={{ 
                flex: 1,
                '& .MuiInputBase-root': { color: 'text.primary' },
                '& .MuiInputLabel-root': { color: 'text.secondary' },
              }}
            />
            <TextField
              label="Beschreibung"
              value={newOffer.description}
              onChange={(e) => setNewOffer({ ...newOffer, description: e.target.value })}
              sx={{ 
                flex: 2,
                '& .MuiInputBase-root': { color: 'text.primary' },
                '& .MuiInputLabel-root': { color: 'text.secondary' },
              }}
            />
            <Button type="submit" variant="contained" color="primary">
              Angebot erstellen
            </Button>
          </Box>
        </Paper>

        {offersLoading ? (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, color: 'text.primary' }}>
            <CircularProgress sx={{ color: 'primary.main' }} />
            <Typography sx={{ color: 'text.primary' }}>Laden...</Typography>
          </Box>
        ) : (
          <TableContainer component={Paper} sx={{ bgcolor: 'background.paper' }}>
            <Table aria-label="offers table">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Titel</TableCell>
                  <TableCell>Beschreibung</TableCell>
                  <TableCell>Aktionen</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {offers.map((o) => (
                  <TableRow key={o.id} sx={{ '&:hover': { bgcolor: 'rgba(124, 58, 237, 0.08)' } }}>
                    <TableCell>{o.id}</TableCell>
                    <TableCell>{o.title}</TableCell>
                    <TableCell>{o.description}</TableCell>
                    <TableCell>
                      <Button
                        variant="outlined"
                        color="primary"
                        onClick={() => handleEditOffer(o)}
                        sx={{ mr: 1 }}
                      >
                        Bearbeiten
                      </Button>
                      <Button
                        variant="outlined"
                        color="primary"
                        onClick={() => handleDeleteOffer(o.id)}
                      >
                        Löschen
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </TabPanel>

      {/* Tab 3: Benutzerrollen */}
      <TabPanel value={tabValue} index={2}>
        {usersLoading ? (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, color: 'text.primary' }}>
            <CircularProgress sx={{ color: 'primary.main' }} />
            <Typography sx={{ color: 'text.primary' }}>Laden...</Typography>
          </Box>
        ) : (
          <TableContainer component={Paper} sx={{ bgcolor: 'background.paper' }}>
            <Table aria-label="users table">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Rolle</TableCell>
                  <TableCell>Aktionen</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((u) => (
                  <TableRow key={u.id} sx={{ '&:hover': { bgcolor: 'rgba(124, 58, 237, 0.08)' } }}>
                    <TableCell>{u.id}</TableCell>
                    <TableCell>{u.username}</TableCell>
                    <TableCell>{u.email}</TableCell>
                    <TableCell>
                      <Box sx={{ 
                        display: 'inline-block',
                        px: 2, 
                        py: 1, 
                        borderRadius: 1,
                        bgcolor: u.role === 'ADMIN' ? 'rgba(124, 58, 237, 0.2)' : 'rgba(124, 58, 237, 0.1)',
                        color: u.role === 'ADMIN' ? '#a78bfa' : '#c4b5fd',
                        fontWeight: 600
                      }}>
                        {u.role}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Button
                        variant="outlined"
                        color="primary"
                        onClick={() => handleToggleRole(u.id, u.role)}
                      >
                        Rolle wechseln
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </TabPanel>

      {/* Edit Offer Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ color: 'text.primary' }}>Angebot bearbeiten</DialogTitle>
        <DialogContent>
          <Box component="form" onSubmit={handleUpdateOffer} sx={{ mt: 2 }}>
            <TextField
              label="Titel"
              value={editOffer?.title || ''}
              onChange={(e) => setEditOffer({ ...editOffer, title: e.target.value })}
              required
              fullWidth
              sx={{ 
                mb: 2,
                '& .MuiInputBase-root': { color: 'text.primary' },
                '& .MuiInputLabel-root': { color: 'text.secondary' },
              }}
            />
            <TextField
              label="Beschreibung"
              value={editOffer?.description || ''}
              onChange={(e) => setEditOffer({ ...editOffer, description: e.target.value })}
              required
              fullWidth
              multiline
              minRows={3}
              sx={{ 
                mb: 2,
                '& .MuiInputBase-root': { color: 'text.primary' },
                '& .MuiInputLabel-root': { color: 'text.secondary' },
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)} color="primary">
            Abbrechen
          </Button>
          <Button onClick={handleUpdateOffer} variant="contained" color="primary">
            Speichern
          </Button>
        </DialogActions>
      </Dialog>
      </Container>
  )
}
