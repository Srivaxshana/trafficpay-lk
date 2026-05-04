import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { initiatePayment, confirmPayment } from '../api/fineApi'

const PAYMENT_METHODS = [
  { value: 'CARD', label: 'Credit / Debit Card' },
  { value: 'ONLINE_BANKING', label: 'Online Banking' },
  { value: 'CASH_ON_SPOT', label: 'Cash on Spot' },
]

export default function PaymentPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { state } = useLocation()
  const fine = state?.fine

  const [name, setName] = useState('')
  const [nic, setNic] = useState('')
  const [method, setMethod] = useState('CARD')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  if (!fine) {
    navigate('/')
    return null
  }

  async function handlePayment(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const initiateRes = await initiatePayment({
        referenceNumber: fine.referenceNumber,
        categoryId: fine.categoryId,
        paymentMethod: method,
        paymentChannel: 'WEB_PORTAL',
        paidByName: name.trim(),
        paidByNic: nic.trim(),
      })

      const { paymentId } = initiateRes.data

      const confirmRes = await confirmPayment({
        paymentId,
        paymentGatewayRef: `mock-gateway-${Date.now()}`,
        status: 'SUCCESS',
      })

      navigate('/confirmation', { state: { receipt: confirmRes.data, fine } })
    } catch (err) {
      const code = err.response?.data?.error
      if (code === 'ALREADY_PAID') {
        setError(t('alreadyPaid'))
      } else {
        setError('Payment failed. Please try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mt-8">
      <div className="flex items-center justify-center gap-2 mb-6">
        {['Lookup Fine', 'Payment', 'Confirmation'].map((step, i) => (
          <div key={step} className="flex items-center gap-2">
            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${
              i + 1 === 2 ? 'bg-blue-900 text-white' : i + 1 < 2 ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-500'
            }`}>{i + 1 < 2 ? '✓' : i + 1}</div>
            <span className={`text-sm hidden sm:block ${i + 1 === 2 ? 'font-semibold text-blue-900' : 'text-gray-400'}`}>{step}</span>
            {i < 2 && <div className="w-8 h-0.5 bg-gray-300" />}
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow p-6 mb-4">
        <h3 className="font-semibold text-gray-700 mb-3">Fine Summary</h3>
        <div className="grid grid-cols-2 gap-2 text-sm">
          <span className="text-gray-500">Reference:</span>
          <span className="font-mono font-medium">{fine.referenceNumber}</span>
          <span className="text-gray-500">Offence:</span>
          <span>{fine.categoryDescription || fine.categoryCode}</span>
          <span className="text-gray-500">Vehicle:</span>
          <span>{fine.vehicleNumber}</span>
          <span className="text-gray-500">Amount:</span>
          <span className="font-bold text-red-600 text-lg">LKR {fine.amount?.toLocaleString()}</span>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow p-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">{t('step2')}</h2>
        <form onSubmit={handlePayment} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{t('payerName')}</label>
            <input
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{t('payerNic')}</label>
            <input
              type="text"
              value={nic}
              onChange={e => setNic(e.target.value)}
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{t('paymentMethod')}</label>
            <select
              value={method}
              onChange={e => setMethod(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {PAYMENT_METHODS.map(m => (
                <option key={m.value} value={m.value}>{m.label}</option>
              ))}
            </select>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">{error}</div>
          )}

          <div className="flex gap-3">
            <button
              type="button"
              onClick={() => navigate('/')}
              className="flex-1 border border-gray-300 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-50 transition"
            >
              Back
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition"
            >
              {loading ? t('loading') : t('payBtn') + ' — LKR ' + fine.amount?.toLocaleString()}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
