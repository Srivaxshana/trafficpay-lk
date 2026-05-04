import { useState, useEffect } from 'react'
import { getAdminFines, cancelFine } from '../api/adminApi'

const STATUS_COLORS = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  PAID: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
}

export default function FinesPage() {
  const [fines, setFines] = useState([])
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState('')
  const [district, setDistrict] = useState('')
  const [cancelId, setCancelId] = useState(null)
  const [reason, setReason] = useState('')
  const [cancelling, setCancelling] = useState(false)

  async function fetchFines() {
    setLoading(true)
    try {
      const params = {}
      if (status) params.status = status
      if (district) params.district = district
      const { data } = await getAdminFines(params)
      setFines(data.fines || [])
    } catch {
      setFines([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchFines() }, [])

  async function handleCancel(e) {
    e.preventDefault()
    if (!reason.trim()) return
    setCancelling(true)
    try {
      await cancelFine(cancelId, reason)
      setCancelId(null)
      setReason('')
      fetchFines()
    } catch {
      alert('Cancel failed. Please try again.')
    } finally {
      setCancelling(false)
    }
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-gray-800">Fines Management</h1>

      <div className="bg-white rounded-xl shadow p-4 flex flex-wrap gap-3">
        <select value={status} onChange={e => setStatus(e.target.value)}
          className="border rounded-lg px-3 py-2 text-sm">
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="PAID">Paid</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
        <input value={district} onChange={e => setDistrict(e.target.value.toUpperCase())}
          placeholder="District (e.g. COLOMBO)"
          className="border rounded-lg px-3 py-2 text-sm" />
        <button onClick={fetchFines}
          className="bg-blue-900 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-800">
          Search
        </button>
      </div>

      {loading ? (
        <div className="text-center py-20 text-gray-400">Loading...</div>
      ) : (
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50">
                <tr>
                  {['Reference', 'Vehicle', 'Driver', 'Category', 'Amount', 'District', 'Status', 'Issued At', 'Actions'].map(h => (
                    <th key={h} className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {fines.map(fine => (
                  <tr key={fine.fineId} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-mono text-xs">{fine.referenceNumber}</td>
                    <td className="px-4 py-3">{fine.vehicleNumber}</td>
                    <td className="px-4 py-3">{fine.driverName}</td>
                    <td className="px-4 py-3">{fine.categoryCode}</td>
                    <td className="px-4 py-3 font-medium">LKR {fine.amount?.toLocaleString()}</td>
                    <td className="px-4 py-3">{fine.district}</td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${STATUS_COLORS[fine.status] || ''}`}>
                        {fine.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-400">
                      {fine.issuedAt ? new Date(fine.issuedAt).toLocaleDateString() : '-'}
                    </td>
                    <td className="px-4 py-3">
                      {fine.status === 'PENDING' && (
                        <button onClick={() => setCancelId(fine.fineId)}
                          className="text-red-600 hover:text-red-800 text-xs font-medium">
                          Cancel
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {fines.length === 0 && (
                  <tr>
                    <td colSpan={9} className="text-center py-10 text-gray-400">No fines found</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {cancelId && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md">
            <h3 className="font-semibold text-gray-800 mb-4">Cancel Fine</h3>
            <form onSubmit={handleCancel} className="space-y-4">
              <textarea
                value={reason}
                onChange={e => setReason(e.target.value)}
                placeholder="Reason for cancellation..."
                required
                rows={3}
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
              />
              <div className="flex gap-3">
                <button type="button" onClick={() => { setCancelId(null); setReason('') }}
                  className="flex-1 border border-gray-300 rounded-lg py-2 text-sm">
                  Back
                </button>
                <button type="submit" disabled={cancelling}
                  className="flex-1 bg-red-600 text-white rounded-lg py-2 text-sm disabled:opacity-50">
                  {cancelling ? 'Cancelling...' : 'Confirm Cancel'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
