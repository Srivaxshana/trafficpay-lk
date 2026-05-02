import { Routes, Route } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import HomePage from './pages/HomePage'
import PaymentPage from './pages/PaymentPage'
import ConfirmationPage from './pages/ConfirmationPage'

const LANGS = [
  { code: 'en', label: 'English' },
  { code: 'si', label: 'සිංහල' },
  { code: 'ta', label: 'தமிழ்' },
]

export default function App() {
  const { t, i18n } = useTranslation()

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-blue-900 text-white px-6 py-4 flex justify-between items-center shadow">
        <div>
          <h1 className="text-xl font-bold">🚔 {t('title')}</h1>
          <p className="text-xs opacity-75">Sri Lanka Police Department</p>
        </div>
        <div className="flex gap-2">
          {LANGS.map(lang => (
            <button
              key={lang.code}
              onClick={() => i18n.changeLanguage(lang.code)}
              className={`px-3 py-1 rounded text-sm font-medium transition ${
                i18n.language === lang.code
                  ? 'bg-white text-blue-900'
                  : 'border border-white text-white hover:bg-blue-800'
              }`}
            >
              {lang.label}
            </button>
          ))}
        </div>
      </header>

      <main className="flex-1 max-w-2xl mx-auto w-full p-4">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/pay" element={<PaymentPage />} />
          <Route path="/confirmation" element={<ConfirmationPage />} />
        </Routes>
      </main>

      <footer className="bg-gray-800 text-white text-center py-3 text-xs">
        © 2024 Sri Lanka Police Department | Traffic Fine Management System
      </footer>
    </div>
  )
}
