// RegisterPage.jsx
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./RegisterPage.css";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [selected, setSelected] = useState(null); // "customer" | "company" | "admin"

  function onContinue() {
    if (!selected) return;

    if (selected === "customer") navigate("/register/customer");
    if (selected === "company") navigate("/register/company");
    if (selected === "admin") navigate("/register/admin"); // admin ayrı ekran use-case
  }

  function onCancel() {
    navigate("/login");
  }

  return (
    <div className="registerPage">
      <div className="registerCard">
        <header className="header">
          <h1 className="title">
            Shu<span>Bilet</span>
          </h1>
          <p className="subtitle">Kayıt türünü seç</p>
        </header>

        <div className="options" role="radiogroup" aria-label="Kullanıcı türü seçimi">
          <button
            type="button"
            className={`option ${selected === "customer" ? "active" : ""}`}
            onClick={() => setSelected("customer")}
            role="radio"
            aria-checked={selected === "customer"}
          >
            <div className="optionTop">
              <span className="badge">Customer</span>
              <span className="pill">Bilet satın al</span>
            </div>
            <p className="optionDesc">
              Sefer ara, koltuk seç, bilet satın al ve rezervasyonlarını yönet.
            </p>
          </button>

          <button
            type="button"
            className={`option ${selected === "company" ? "active" : ""}`}
            onClick={() => setSelected("company")}
            role="radio"
            aria-checked={selected === "company"}
          >
            <div className="optionTop">
              <span className="badge">Company</span>
              <span className="pill">Sefer yayınla</span>
            </div>
            <p className="optionDesc">
              Sefer oluştur, fiyatlandır, satışları takip et ve raporları görüntüle.
            </p>
          </button>

          <button
            type="button"
            className={`option ${selected === "admin" ? "active" : ""}`}
            onClick={() => setSelected("admin")}
            role="radio"
            aria-checked={selected === "admin"}
          >
            <div className="optionTop">
              <span className="badge">Admin</span>
              <span className="pill">Sistem yönetimi</span>
            </div>
            <p className="optionDesc">
              Kullanıcıları/şirketleri yönet, sistem ayarlarını kontrol et.
            </p>
          </button>
        </div>

        <div className="actions">
          <button className="primaryButton" type="button" disabled={!selected} onClick={onContinue}>
            Devam Et
          </button>

          <button className="ghostButton" type="button" onClick={onCancel}>
            Cancel
          </button>

          <p className="footerText">
            Zaten hesabın var mı?{" "}
            <Link className="link" to="/login">
              Login
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
