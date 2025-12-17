// src/components/CustomerTopBar.jsx
import { NavLink, useNavigate } from "react-router-dom";
import "./CustomerTopBar.css";

export default function CustomerTopBar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // TODO: Backend logout varsa buraya bağlanır
    localStorage.clear();
    navigate("/login", { replace: true });
  };

  const linkClass = ({ isActive }) =>
    `navLink ${isActive ? "active" : ""}`;

  return (
    <header className="customerTopBar">
      <div className="left">
        <NavLink to="/customer/home" className="brand">
          Shu<span>Bilet</span>
        </NavLink>
      </div>

      <nav className="right">
        <NavLink to="/travel" className={linkClass}>
          Travel
        </NavLink>

        <NavLink to="/my-tickets" className={linkClass}>
          My Tickets
        </NavLink>

        <NavLink to="/profile" className={linkClass}>
          Profile
        </NavLink>

        <button className="logoutBtn" onClick={handleLogout}>
          Logout
        </button>
      </nav>
    </header>
  );
}