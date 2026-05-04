import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { lookupFine } from '../services/api'

export default function FineSearch() {
  const [referenceNumber, setReferenceNumber] = useState('')
  const [categoryCode, setCategoryCode] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSearch = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await lookupFine(referenceNumber.trim().toUpperCase(), categoryCode.trim().toUpperCase())
      navigate('/pay', { state: { fine: data } })
    } catch (err) {
      setError(err.response?.data?.message || 'Fine not found. Please check the reference number and category code.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card">
      <h2 className="card-title">Pay Your Traffic Fine</h2>
      <p className="card-subtitle">Enter the details from your fine sheet issued by the traffic police officer.</p>

      <form onSubmit={handleSearch} className="form">
        <div className="form-group">
          <label>Fine Reference Number</label>
          <input
            type="text"
            placeholder="e.g. TF-A1B2C3D4"
            value={referenceNumber}
            onChange={e => setReferenceNumber(e.target.value)}
            required
            className="input"
          />
        </div>
        <div className="form-group">
          <label>Fine Category Code</label>
          <input
            type="text"
            placeholder="e.g. TC001"
            value={categoryCode}
            onChange={e => setCategoryCode(e.target.value)}
            required
            className="input"
          />
          <small className="hint">Category codes: TC001=Speeding, TC002=Red Light, TC003=No Seat Belt, TC004=Mobile While Driving, TC005=Drunk Driving, TC006=No License, TC007=Illegal Parking, TC008=No Insurance</small>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Searching...' : 'Find Fine'}
        </button>
      </form>
    </div>
  )
}
