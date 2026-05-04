import { useLocation, useNavigate } from 'react-router-dom'

export default function PaymentSuccess() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const { payment, fine } = state || {}

  if (!payment) {
    navigate('/')
    return null
  }

  const paidDate = payment.paidAt ? new Date(payment.paidAt).toLocaleString() : new Date().toLocaleString()

  return (
    <div className="card success-card">
      <div className="success-icon">✅</div>
      <h2 className="card-title">Payment Successful!</h2>
      <p className="card-subtitle">Your fine has been paid and the officer has been notified via SMS.</p>

      <div className="fine-details receipt">
        <div className="detail-row">
          <span className="detail-label">Transaction ID</span>
          <span className="detail-value ref">{payment.transactionId}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Fine Reference</span>
          <span className="detail-value">{payment.referenceNumber}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Violation</span>
          <span className="detail-value">{fine?.categoryName}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Amount Paid</span>
          <span className="detail-value amount">LKR {payment.amount?.toLocaleString()}.00</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Payment Method</span>
          <span className="detail-value">{payment.paymentMethod}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Date & Time</span>
          <span className="detail-value">{paidDate}</span>
        </div>
      </div>

      <div className="alert alert-info">
        The traffic police officer has been notified. You may now retrieve your driving license.
      </div>

      <button className="btn btn-primary" onClick={() => navigate('/')}>Pay Another Fine</button>
    </div>
  )
}
