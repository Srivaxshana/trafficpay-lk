import { useLocation, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { jsPDF } from 'jspdf'

export default function ConfirmationPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { state } = useLocation()
  const receipt = state?.receipt
  const fine = state?.fine

  if (!receipt) {
    navigate('/')
    return null
  }

  function downloadReceipt() {
    const doc = new jsPDF()
    doc.setFontSize(18)
    doc.text('SL Police - Traffic Fine Receipt', 20, 20)
    doc.setFontSize(12)
    doc.text(`Receipt No: ${receipt.receiptNumber}`, 20, 40)
    doc.text(`Reference: ${receipt.referenceNumber}`, 20, 50)
    doc.text(`Amount Paid: LKR ${receipt.amount?.toLocaleString()}`, 20, 60)
    doc.text(`Vehicle: ${fine?.vehicleNumber || '-'}`, 20, 70)
    doc.text(`Payment Method: ${receipt.status}`, 20, 80)
    doc.text(`Paid At: ${receipt.paidAt ? new Date(receipt.paidAt).toLocaleString() : new Date().toLocaleString()}`, 20, 90)
    doc.text('Officer has been notified via SMS.', 20, 110)
    doc.text('You may now retrieve your licence.', 20, 120)
    doc.save(`receipt-${receipt.receiptNumber}.pdf`)
  }

  return (
    <div className="mt-8">
      <div className="flex items-center justify-center gap-2 mb-6">
        {['Lookup Fine', 'Payment', 'Confirmation'].map((step, i) => (
          <div key={step} className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold bg-green-500 text-white">✓</div>
            <span className={`text-sm hidden sm:block ${i + 1 === 3 ? 'font-semibold text-green-600' : 'text-gray-400'}`}>{step}</span>
            {i < 2 && <div className="w-8 h-0.5 bg-green-300" />}
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow p-8 text-center" id="receipt-area">
        <div className="text-5xl mb-4">✅</div>
        <h2 className="text-2xl font-bold text-green-600 mb-2">{t('success')}</h2>
        <p className="text-gray-500 mb-6">The issuing officer has been notified via SMS. You may now retrieve your driving licence.</p>

        <div className="bg-gray-50 rounded-lg p-4 text-left text-sm space-y-2 mb-6">
          <div className="flex justify-between">
            <span className="text-gray-500">Receipt No</span>
            <span className="font-mono font-semibold">{receipt.receiptNumber}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Reference</span>
            <span className="font-mono">{receipt.referenceNumber}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Amount Paid</span>
            <span className="font-bold text-green-600">LKR {receipt.amount?.toLocaleString()}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Vehicle</span>
            <span>{fine?.vehicleNumber}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Date & Time</span>
            <span>{receipt.paidAt ? new Date(receipt.paidAt).toLocaleString() : new Date().toLocaleString()}</span>
          </div>
        </div>

        <div className="flex gap-3">
          <button
            onClick={downloadReceipt}
            className="flex-1 bg-blue-900 text-white py-3 rounded-lg font-semibold hover:bg-blue-800 transition"
          >
            📄 {t('downloadReceipt')}
          </button>
          <button
            onClick={() => navigate('/')}
            className="flex-1 border border-gray-300 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-50 transition"
          >
            Pay Another Fine
          </button>
        </div>
      </div>
    </div>
  )
}
