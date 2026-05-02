import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { lookupFine } from '../api/fineApi'

export default function HomePage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [ref, setRef] = useState('')
  const [cat, setCat] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function handleLookup(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const { data } = await lookupFine(ref.trim(), cat.trim())
      navigate('/pay', { state: { fine: data } })
    } catch (err) {
      const status = err.response?.status
      const code = err.response?.data?.error
      if (status === 409 || code === 'ALREADY_PAID') {
        setError(t('alreadyPaid'))
      } else if (status === 404) {
        setError(t('notFound'))
      } else {
        setError('An error occurred. Please try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mt-8">
      <StepIndicator current={1} />

      <div className="bg-white rounded-xl shadow p-6 mt-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-1">{t('step1')}</h2>
        <p className="text-gray-500 text-sm mb-6">
          Enter the reference number and category ID from your fine sheet.
        </p>

        <form onSubmit={handleLookup} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{t('refNumber')}</label>
            <input
              type="text"
              value={ref}
              onChange={e => setRef(e.target.value)}
              placeholder="TF-2024-001234"
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 uppercase"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{t('categoryId')}</label>
            <input
              type="text"
              value={cat}
              onChange={e => setCat(e.target.value)}
              placeholder="CAT-001"
              required
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 uppercase"
            />
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-900 text-white py-3 rounded-lg font-semibold hover:bg-blue-800 disabled:opacity-50 transition"
          >
            {loading ? t('loading') : t('lookupBtn')}
          </button>
        </form>
      </div>

      <p className="text-center text-xs text-gray-400 mt-6">
        For assistance, contact Sri Lanka Police: <strong>119</strong>
      </p>
    </div>
  )
}

function StepIndicator({ current }) {
  const steps = ['Lookup Fine', 'Payment', 'Confirmation']
  return (
    <div className="flex items-center justify-center gap-2">
      {steps.map((step, i) => (
        <div key={step} className="flex items-center gap-2">
          <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${
            i + 1 === current ? 'bg-blue-900 text-white' :
            i + 1 < current ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-500'
          }`}>{i + 1 < current ? '✓' : i + 1}</div>
          <span className={`text-sm hidden sm:block ${i + 1 === current ? 'font-semibold text-blue-900' : 'text-gray-400'}`}>
            {step}
          </span>
          {i < steps.length - 1 && <div className="w-8 h-0.5 bg-gray-300" />}
        </div>
      ))}
    </div>
  )
}
