import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Layout() {
  const { user, logoutUser } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logoutUser()
    navigate('/login')
  }

  const navItems = [
    { to: '/dashboard', label: '📊 Dashboard' },
    { to: '/fines', label: '📋 Fines' },
    { to: '/officers', label: '👮 Officers' },
  ]

  return (
    <div className="flex h-screen bg-gray-100">
      <aside className="w-64 bg-blue-900 text-white flex flex-col">
        <div className="p-6 border-b border-blue-800">
          <h1 className="text-lg font-bold">SL Traffic Police</h1>
          <p className="text-xs text-blue-300 mt-1">Admin Portal</p>
        </div>
        <nav className="flex-1 p-4 space-y-1">
          {navItems.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `block px-4 py-2.5 rounded-lg text-sm font-medium transition ${
                  isActive ? 'bg-white text-blue-900' : 'text-blue-100 hover:bg-blue-800'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t border-blue-800">
          <p className="text-xs text-blue-300 mb-1">{user?.fullName}</p>
          <p className="text-xs text-blue-400 mb-3">{user?.district}</p>
          <button
            onClick={handleLogout}
            className="w-full text-sm text-blue-200 hover:text-white py-1"
          >
            Logout
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-auto">
        <div className="p-6">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
