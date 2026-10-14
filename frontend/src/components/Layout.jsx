import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Layout() {
  const { user, logout } = useAuth();

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="brand-block">
          <h2>TaskFlow</h2>
          <p>{user?.name || 'User'}</p>
        </div>
        <nav className="nav">
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/tasks/new">New Task</NavLink>
          {user?.role === 'ADMIN' && <NavLink to="/admin">Admin</NavLink>}
        </nav>
        <button className="logout-button" onClick={logout}>Logout</button>
      </aside>
      <main className="content-area">
        <Outlet />
      </main>
    </div>
  );
}
