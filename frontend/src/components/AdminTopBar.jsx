import { NavLink, useNavigate } from "react-router-dom";
import "./AdminTopBar.css";

export default function AdminTopBar() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (_) {}

    navigate("/login", { replace: true });
  };

  const linkClass = ({ isActive }) =>
    `navLink ${isActive ? "active" : ""}`;

  return (
    <header className="adminTopBar">
      <NavLink to="/admin/home" className="brand">
        Shu<span>Bilet</span>
      </NavLink>

      <nav className="nav">
        <NavLink to="/admin/confirm-company" className={linkClass}>
          Company Confirm
        </NavLink>

        <NavLink to="/admin/confirm-admin" className={linkClass}>
          Admin Confirm
        </NavLink>

        <button className="logoutBtn" onClick={handleLogout}>
          Logout
        </button>
      </nav>
    </header>
  );
}
