import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { payFine } from '../services/api'

export default function PaymentForm() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const fine = state?.fine

  const [form, setForm] = useState({
    cardHolderName: '',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  if (!fine) {
    navigate('/')
    return null
  }

  const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await payFine({
        referenceNumber: fine.referenceNumber,
        categoryCode: fine.categoryCode,
        cardHolderName: form.cardHolderName,
        cardNumber: form.cardNumber,
        expiryDate: form.expiryDate,
        cvv: form.cvv,
        paymentMethod: 'CARD',
      })
      navigate('/success', { state: { payment: data, fine } })
    } catch (err) {
      setError(err.response?.data?.error || 'Payment failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card">
      <h2 className="card-title">Fine Details & Payment</h2>

      <div className="fine-details">
        <div className="detail-row">
          <span className="detail-label">Reference Number</span>
          <span className="detail-value ref">{fine.referenceNumber}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Violation</span>
          <span className="detail-value">{fine.categoryName}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Vehicle</span>
          <span className="detail-value">{fine.vehicleNumber}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Driver</span>
          <span className="detail-value">{fine.driverName}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">District</span>
          <span className="detail-value">{fine.district}</span>
        </div>
        <div className="detail-row amount-row">
          <span className="detail-label">Amount Due</span>
          <span className="detail-value amount">LKR {fine.amount?.toLocaleString()}.00</span>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="form">
        <h3 className="section-title">Card Details</h3>
        <div className="form-group">
          <label>Card Holder Name</label>
          <input name="cardHolderName" value={form.cardHolderName} onChange={handleChange}
            placeholder="Name on card" required className="input" />
        </div>
        <div className="form-group">
          <label>Card Number</label>
          <input name="cardNumber" value={form.cardNumber} onChange={handleChange}
            placeholder="1234 5678 9012 3456" maxLength={19} required className="input" />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Expiry Date</label>
            <input name="expiryDate" value={form.expiryDate} onChange={handleChange}
              placeholder="MM/YY" maxLength={5} required className="input" />
          </div>
          <div className="form-group">
            <label>CVV</label>
            <input name="cvv" value={form.cvv} onChange={handleChange}
              placeholder="123" maxLength={3} required className="input" />
          </div>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <div className="btn-group">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/')}>Back</button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Processing...' : `Pay LKR ${fine.amount?.toLocaleString()}.00`}
          </button>
        </div>
      </form>
    </div>
  )
}
