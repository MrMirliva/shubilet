// src/pages/registerCompany/CompanyRegister.jsx
import { useState } from "react";
import { Link } from "react-router-dom";
import "./CompanyRegister.css";

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidPassword(pw) {
  return pw.length >= 8 && /[A-Za-z]/.test(pw) && /\d/.test(pw);
}

function isValidCompanyName(name) {
  return name.trim().length >= 2;
}

export default function CompanyRegister() {
  const [form, setForm] = useState({
    companyName: "",
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState({});
  const [submitted, setSubmitted] = useState(false);

  function validate() {
    const e = {};
    if (!form.companyName) e.companyName = "Company Name zorunlu.";
    else if (!isValidCompanyName(form.companyName))
      e.companyName = "Geçerli bir şirket adı giriniz.";

    if (!form.email) e.email = "E-mail zorunlu.";
    else if (!isValidEmail(form.email))
      e.email = "Please enter a valid e-mail address.";

    if (!form.password) e.password = "Password zorunlu.";
    else if (!isValidPassword(form.password))
      e.password = "Password en az 8 karakter, harf + rakam içermeli.";

    setErrors(e);
    return Object.keys(e).length === 0;
  }

  function onSubmit(e) {
    e.preventDefault();
    if (!validate()) return;

    // TODO: Backend entegrasyonu
    // Company account -> Pending Approval

    setSubmitted(true);
  }

  if (submitted) {
    return (
      <div className="companyRegPage withBusBg">
        <div className="companyRegCard">
          <h2 className="infoTitle">Başvurun Alındı</h2>
          <p className="infoText">
            Şirket hesabın <strong>Pending Approval</strong> durumunda.
            <br />
            İnceleme tamamlandığında seninle iletişime geçilecektir.
          </p>

          <Link to="/login" className="primaryButton">
            Login Sayfasına Dön
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="companyRegPage withBusBg">
      <div className="companyRegCard">
        <header className="header">
          <h1 className="title">
            Company <span>Sign Up</span>
          </h1>
          <p className="subtitle">Şirket bilgilerini gir</p>
        </header>

        <form className="form" onSubmit={onSubmit} noValidate>
          <div className="field">
            <label>Company Name</label>
            <input
              className="input"
              value={form.companyName}
              onChange={(e) =>
                setForm({ ...form, companyName: e.target.value })
              }
            />
            {errors.companyName && (
              <div className="error">{errors.companyName}</div>
            )}
          </div>

          <div className="field">
            <label>E-mail</label>
            <input
              className="input"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
            {errors.email && <div className="error">{errors.email}</div>}
          </div>

          <div className="field">
            <label>Password</label>
            <input
              type="password"
              className="input"
              value={form.password}
              onChange={(e) =>
                setForm({ ...form, password: e.target.value })
              }
            />
            {errors.password && (
              <div className="error">{errors.password}</div>
            )}
          </div>

          <button className="primaryButton" type="submit">
            Create Company Account
          </button>

          <p className="footerText">
            Vazgeçmek ister misin?{" "}
            <Link className="link" to="/register">
              Geri dön
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}
