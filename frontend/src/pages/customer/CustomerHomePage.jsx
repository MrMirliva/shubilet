// CustomerHomePage.jsx
import { NavLink, useNavigate } from "react-router-dom";
import "./CustomerHomePage.css";

export default function CustomerHomePage() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    // TODO: ekipte logout API nasıl yapıldıysa buraya bağla (ör: api.logout())
    // Şimdilik local token temizliği + login'e dönüş:
    try {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("user");
    } catch (_) {}
    navigate("/login", { replace: true });
  };

  const linkClass = ({ isActive }) =>
    `navLink ${isActive ? "active" : ""}`.trim();

  return (
    <div className="customerHome withBusBg">
      <header className="topBar">
        <NavLink to="/customer/home" className="brandLink" aria-label="ShuBilet Ana Sayfa">
          <div className="brand">
            Shu<span>Bilet</span>
          </div>
        </NavLink>

        <nav className="nav">
          <NavLink to="/travel" className={linkClass}>
            Travel
          </NavLink>
          <NavLink to="/my-tickets" className={linkClass}>
            My Tickets
          </NavLink>
          <NavLink to="/profile" className={linkClass}>
            Profile
          </NavLink>

          <button type="button" className="logoutBtn" onClick={handleLogout}>
            Logout
          </button>
        </nav>
      </header>

      <main className="content">
        <div className="card">
          <div className="hero">
            <h1 className="title">Hoş geldin 👋</h1>
            <p className="subtitle">
              Travel bölümünden sefer arayabilir, My Tickets’ta biletlerini yönetebilir,
              Profile’dan bilgilerini güncelleyebilirsin.
            </p>
          </div>

          <div className="quickGrid">
            <button className="quickCard" type="button" onClick={() => navigate("/travel")}>
              <div className="quickTop">
                <div className="quickTitle">Travel</div>
                <div className="pill">Sefer ara</div>
              </div>
              <div className="quickDesc">Kalkış-varış seç, tarih belirle, biletini al.</div>
            </button>

            <button className="quickCard" type="button" onClick={() => navigate("/my-tickets")}>
              <div className="quickTop">
                <div className="quickTitle">My Tickets</div>
                <div className="pill">Biletlerim</div>
              </div>
              <div className="quickDesc">Aktif biletlerini görüntüle, detayları kontrol et.</div>
            </button>

            <button className="quickCard" type="button" onClick={() => navigate("/profile")}>
              <div className="quickTop">
                <div className="quickTitle">Profile</div>
                <div className="pill">Hesabım</div>
              </div>
              <div className="quickDesc">Kişisel bilgilerini ve ayarlarını yönet.</div>
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
