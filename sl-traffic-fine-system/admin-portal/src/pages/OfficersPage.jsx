import { useState } from 'react'
import { createOfficer } from '../api/adminApi'

const DISTRICTS = [
  'COLOMBO','GAMPAHA','KALUTARA','KANDY','MATALE','NUWARA ELIYA','GALLE','MATARA',
  'HAMBANTOTA','JAFFNA','KILINOCHCHI','MANNAR','VAVUNIYA','MULLAITIVU','BATTICALOA',
  'AMPARA','TRINCOMALEE','KURUNEGALA','PUTTALAM','ANURADHAPURA','POLONNARUWA',
  'BADULLA','MONARAGALA','RATNAPURA','KEGALLE'
]

const EMPTY_FORM = {
  fullName: '', badgeNumber: '', phoneNumber: '', email: '',
  district: 'COLOMBO', station: '', password: ''
}

export default function OfficersPage() {
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(EMPTY_FORM)
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(null)
  const [error, setError] = useState(null)

  function handleChange(e) {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSuccess(null)
    setLoading(true)
    try {
      const { data } = await createOfficer(form)
      setSuccess(`Officer ${data.fullName} (${data.badgeNumber}) created successfully.`)
      setForm(EMPTY_FORM)
      setShowForm(false)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create officer.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Officers Management</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-900 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-800"
        >
          + Add Officer
        </button>
      </div>

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 rounded-lg px-4 py-3 text-sm">
          {success}
        </div>
      )}

      {showForm && (
        <div className="bg-white rounded-xl shadow p-6">
          <h2 className="font-semibold text-gray-800 mb-4">Register New Officer</h2>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {[
              { name: 'fullName', label: 'Full Name', type: 'text', required: true },
              { name: 'badgeNumber', label: 'Badge Number', type: 'text', required: true, placeholder: 'SP-0042' },
              { name: 'phoneNumber', label: 'Phone Number', type: 'tel', required: true, placeholder: '+94771234567' },
              { name: 'email', label: 'Email', type: 'email', required: false },
              { name: 'station', label: 'Police Station', type: 'text', required: true },
              { name: 'password', label: 'Initial Password', type: 'password', required: true },
            ].map(field => (
              <div key={field.name}>
                <label className="block text-sm font-medium text-gray-700 mb-1">{field.label}</label>
                <input
                  type={field.type}
                  name={field.name}
                  value={form[field.name]}
                  onChange={handleChange}
                  required={field.required}
                  placeholder={field.placeholder}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            ))}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">District</label>
              <select name="district" value={form.district} onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                {DISTRICTS.map(d => <option key={d} value={d}>{d}</option>)}
              </select>
            </div>

            {error && (
              <div className="col-span-2 bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">{error}</div>
            )}

            <div className="col-span-2 flex gap-3">
              <button type="button" onClick={() => setShowForm(false)}
                className="flex-1 border border-gray-300 rounded-lg py-2 text-sm hover:bg-gray-50">
                Cancel
              </button>
              <button type="submit" disabled={loading}
                className="flex-1 bg-blue-900 text-white rounded-lg py-2 text-sm hover:bg-blue-800 disabled:opacity-50">
                {loading ? 'Creating...' : 'Create Officer'}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="bg-white rounded-xl shadow p-6">
        <p className="text-gray-500 text-sm">
          Use the form above to register new traffic police officers.
          Each officer will be able to log in to the Android app using their badge number and initial password.
        </p>
      </div>
    </div>
  )
}
