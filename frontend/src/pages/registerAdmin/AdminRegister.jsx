// src/pages/registerAdmin/AdminRegister.jsx
import { useState } from "react";
import { Link } from "react-router-dom";
import "./AdminRegister.css";

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidPassword(pw) {
  return pw.length >= 8 && /[A-Za-z]/.test(pw) && /\d/.test(pw);
}

function isValidName(name) {
  const t = name.trim();
  if (t.length < 2) return false;
  return /^[A-Za-zÇĞİÖŞÜçğıöşü\s]+$/.test(t);
}

export default function AdminRegister() {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState({});
  const [submitted, setSubmitted] = useState(false);

  function validate() {
    const e = {};
    if (!form.firstName) e.firstName = "First Name zorunlu.";
    else if (!isValidName(form.firstName))
      e.firstName = "En az 2 karakter, sadece harf.";

    if (!form.lastName) e.lastName = "Last Name zorunlu.";
    else if (!isValidName(form.lastName))
      e.lastName = "En az 2 karakter, sadece harf.";

    if (!form.email) e.email = "E-mail zorunlu.";
    else if (!isValidEmail(form.email))
      e.email = "Please enter a valid e-mail address.";

    if (!form.password) e.password = "Password zorunlu.";
    else if (!isValidPassword(form.password))
      e.password = "Min 8 karakter, harf + rakam içermeli.";

    setErrors(e);
    return Object.keys(e).length === 0;
  }

  function onSubmit(e) {
    e.preventDefault();
    if (!validate()) return;

    // TODO: Backend entegrasyonu
    // Admin account -> Pending Approval
    setSubmitted(true);
  }

  if (submitted) {
    return (
      <div className="adminRegPage withBusBg">
        <div className="adminRegCard">
          <h2 className="infoTitle">Başvurun Alındı</h2>
          <p className="infoText">
            Admin başvurun <strong>Pending Approval</strong> durumunda.
            <br />
            İnceleme sonrası bilgilendirileceksin.
          </p>

          <Link to="/login" className="primaryButton">
            Login Sayfasına Dön
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="adminRegPage withBusBg">
      <div className="adminRegCard">
        <header className="header">
          <h1 className="title">
            Admin <span>Sign Up</span>
          </h1>
          <p className="subtitle">Yönetici bilgilerini gir</p>
        </header>

        <form className="form" onSubmit={onSubmit} noValidate>
          <div className="grid">
            <div className="field">
              <label>First Name</label>
              <input
                className="input"
                value={form.firstName}
                onChange={(e) =>
                  setForm({ ...form, firstName: e.target.value })
                }
              />
              {errors.firstName && (
                <div className="error">{errors.firstName}</div>
              )}
            </div>

            <div className="field">
              <label>Last Name</label>
              <input
                className="input"
                value={form.lastName}
                onChange={(e) =>
                  setForm({ ...form, lastName: e.target.value })
                }
              />
              {errors.lastName && (
                <div className="error">{errors.lastName}</div>
              )}
            </div>
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
            Submit Admin Application
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
