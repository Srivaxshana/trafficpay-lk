import { BrowserRouter, Routes, Route } from 'react-router-dom'
import FineSearch from './components/FineSearch'
import PaymentForm from './components/PaymentForm'
import PaymentSuccess from './components/PaymentSuccess'

export default function App() {
  return (
    <BrowserRouter>
      <div className="app-wrapper">
        <header className="app-header">
          <div className="header-inner">
            <span className="badge-icon">🚔</span>
            <div>
              <h1>Sri Lanka Police</h1>
              <p>Traffic Fine Payment Portal</p>
            </div>
          </div>
        </header>
        <main className="app-main">
          <Routes>
            <Route path="/" element={<FineSearch />} />
            <Route path="/pay" element={<PaymentForm />} />
            <Route path="/success" element={<PaymentSuccess />} />
          </Routes>
        </main>
        <footer className="app-footer">
          <p>© 2026 Sri Lanka Police Department. All rights reserved.</p>
        </footer>
      </div>
    </BrowserRouter>
  )
}
