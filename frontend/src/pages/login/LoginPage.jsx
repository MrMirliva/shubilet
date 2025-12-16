// LoginPage.jsx
import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./LoginPage.css";

export default function LoginPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({ email: "", password: "" });
  const [touched, setTouched] = useState({ email: false, password: false });
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [serverError, setServerError] = useState("");

  const errors = useMemo(() => {
    const e = {};
    const email = form.email.trim();
    const password = form.password;

    if (!email) e.email = "E-posta zorunlu.";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
      e.email = "Geçerli bir e-posta gir.";

    if (!password) e.password = "Şifre zorunlu.";
    else if (password.length < 6)
      e.password = "Şifre en az 6 karakter olmalı.";

    return e;
  }, [form]);

  const canSubmit = Object.keys(errors).length === 0 && !isSubmitting;

  function onChange(e) {
    const { name, value } = e.target;
    setForm((p) => ({ ...p, [name]: value }));
    if (serverError) setServerError("");
  }

  function onBlur(e) {
    const { name } = e.target;
    setTouched((p) => ({ ...p, [name]: true }));
  }

  async function onSubmit(e) {
    e.preventDefault();
    setTouched({ email: true, password: true });
    if (!canSubmit) return;

    setIsSubmitting(true);
    setServerError("");

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          email: form.email.trim(),
          password: form.password,
        }),
      });

      // 🔹 Önce raw text alıyoruz (gateway / backend fark etmez)
      const rawText = await response.text();

      let data = null;
      try {
        data = rawText ? JSON.parse(rawText) : null;
      } catch {
        data = null;
      }

      // 🔹 HTTP status kontrolü
      if (!response.ok) {
        const message =
          data?.message ||
          `Login failed (HTTP ${response.status})`;
        throw new Error(message);
      }

      // 🔹 Backend MessageDTO dönüyor → ister logla, ister göster
      console.log("LOGIN SUCCESS:", data?.message);

      // 🔹 Başarılı login → yönlendir
      navigate("/", { replace: true });

    } catch (err) {
      setServerError(
        err?.message || "Giriş yapılamadı. Bilgilerini kontrol et."
      );
    } finally {
      setIsSubmitting(false);
    }
  }


  return (
    <div className="loginPage">
      <div className="loginCard">
        <header className="header">
          <h1 className="title">
            Shu<span>Bilet</span>
          </h1>
          <p className="subtitle">
            Yolculuğunuza başlamak için giriş yapın
          </p>
        </header>

        {serverError && <div className="alert">{serverError}</div>}

        <form className="form" onSubmit={onSubmit} noValidate>
          <div className="field">
            <label>E-posta Adresi</label>
            <input
              name="email"
              type="email"
              placeholder="ornek@email.com"
              value={form.email}
              onChange={onChange}
              onBlur={onBlur}
              className={touched.email && errors.email ? "input error" : "input"}
            />
            {touched.email && errors.email && (
              <span className="errorText">{errors.email}</span>
            )}
          </div>

          <div className="field">
            <label>Şifre</label>
            <input
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="••••••••"
              value={form.password}
              onChange={onChange}
              onBlur={onBlur}
              className={
                touched.password && errors.password ? "input error" : "input"
              }
            />
            {touched.password && errors.password && (
              <span className="errorText">{errors.password}</span>
            )}
          </div>

          <button className="primaryButton" disabled={!canSubmit}>
            {isSubmitting ? "Giriş yapılıyor..." : "Giriş Yap"}
          </button>

          <p className="footerText">
            Hesabın yok mu?{" "}
            <Link to="/register" className="link">
              Hemen Kayıt Ol
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}
