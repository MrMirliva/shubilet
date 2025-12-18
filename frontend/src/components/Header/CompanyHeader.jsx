import { Link, useNavigate } from "react-router-dom";
import "./CompanyHeader.css";

export default function CompanyHeader() {
  const navigate = useNavigate();

  async function handleLogout(e) {
    e.preventDefault();
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } finally {
      navigate("/login", { replace: true });
    }
  }

  return (
    <header className="headerBar">
      <Link
        to="/company"
        className="headerBrandLink"
        aria-label="ShuBilet ana sayfa"
      >
        <div className="headerBrand">
          Shu<span>Bilet</span>
        </div>
      </Link>

      <nav className="headerNav">
        <button
          type="button"
          className="headerLink"
          onClick={handleLogout}
        >
          Logout
        </button>
      </nav>
    </header>
  );
}
