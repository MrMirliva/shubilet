// CustomerRegister.jsx
import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./CustomerRegister.css";

const GENDERS = [
  { value: "", label: "Seçiniz" },
  { value: "MALE", label: "Male" },
  { value: "FEMALE", label: "Female" },
  { value: "OTHER", label: "Other" },
];

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidPassword(pw) {
  // en az 8 karakter + hem harf hem rakam
  const hasLetter = /[A-Za-z]/.test(pw);
  const hasNumber = /\d/.test(pw);
  return pw.length >= 8 && hasLetter && hasNumber;
}

function isValidName(name) {
  // en az 2 karakter, sadece harf ve boşluk (Türkçe karakterlere izin)
  // sayı yok, sembol yok
  const trimmed = name.trim();
  if (trimmed.length < 2) return false;
  return /^[A-Za-zÇĞİÖŞÜçğıöşü\s]+$/.test(trimmed);
}

export default function CustomerRegister() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    gender: "",
  });

  const [touched, setTouched] = useState({
    firstName: false,
    lastName: false,
    email: false,
    password: false,
    gender: false,
  });

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [serverError, setServerError] = useState(""); // e.g. email already exists
  const [serverSuccess, setServerSuccess] = useState("");

  const errors = useMemo(() => {
    const e = {};

    if (!form.firstName.trim()) e.firstName = "First Name zorunlu.";
    else if (!isValidName(form.firstName)) e.firstName = "Geçerli bir isim gir (en az 2 harf, sadece harf).";

    if (!form.lastName.trim()) e.lastName = "Last Name zorunlu.";
    else if (!isValidName(form.lastName)) e.lastName = "Geçerli bir soyisim gir (en az 2 harf, sadece harf).";

    if (!form.email.trim()) e.email = "E-mail zorunlu.";
    else if (!isValidEmail(form.email.trim())) e.email = "Please enter a valid e-mail address.";

    if (!form.password) e.password = "Password zorunlu.";
    else if (!isValidPassword(form.password)) e.password = "Invalid password format (min 8, letters + numbers).";

    if (!form.gender) e.gender = "Gender zorunlu.";

    return e;
  }, [form]);

  const canSubmit = Object.keys(errors).length === 0 && !isSubmitting;

  function onChange(e) {
    const { name, value } = e.target;
    setForm((p) => ({ ...p, [name]: value }));
    if (serverError) setServerError("");
    if (serverSuccess) setServerSuccess("");
  }

  function onBlur(e) {
    const { name } = e.target;
    setTouched((p) => ({ ...p, [name]: true }));
  }

  function markAllTouched() {
    setTouched({
      firstName: true,
      lastName: true,
      email: true,
      password: true,
      gender: true,
    });
  }

  async function onSubmit(e) {
    e.preventDefault();
    markAllTouched();
    if (!canSubmit) return;

    setIsSubmitting(true);
    setServerError("");
    setServerSuccess("");

    try {
      // TODO: Backend entegrasyonu
      // - email uniqueness kontrolü (409/400 vs)
      // - başarılı olunca: otomatik login + yönlendirme (Use Case Exit Condition)
      //
      // Örnek:
      // const res = await api.registerCustomer(form)
      // await api.login(form.email, form.password)
      // navigate("/home", { replace: true })

      await new Promise((r) => setTimeout(r, 650)); // demo

      // Demo: “otomatik login oldu” varsayalım
      setServerSuccess("Hesabın oluşturuldu. Oturum açılıyor...");
      setTimeout(() => navigate("/"), 500);
    } catch (err) {
      // Demo: email already exists senaryosu
      setServerError("An account with this e-mail already exists");
    } finally {
      setIsSubmitting(false);
    }
  }

  function onCancel() {
    // Use case: user cancels -> exits interface
    navigate("/register");
  }

  return (
    <div className="customerRegPage">
      <div className="customerRegCard">
        <header className="header">
          <h1 className="title">
            Customer <span>Sign Up</span>
          </h1>
          <p className="subtitle">Bilgilerini girerek hesabını oluştur.</p>
        </header>

        {serverError ? (
          <div className="alert alertError" role="alert" aria-live="polite">
            {serverError}
          </div>
        ) : null}

        {serverSuccess ? (
          <div className="alert alertOk" role="status" aria-live="polite">
            {serverSuccess}
          </div>
        ) : null}

        <form className="form" onSubmit={onSubmit} noValidate>
          <div className="grid">
            <div className="field">
              <label className="label" htmlFor="firstName">
                First Name
              </label>
              <input
                id="firstName"
                name="firstName"
                className={`input ${touched.firstName && errors.firstName ? "inputError" : ""}`}
                value={form.firstName}
                onChange={onChange}
                onBlur={onBlur}
                placeholder="Örn: Ömer"
                autoComplete="given-name"
              />
              {touched.firstName && errors.firstName ? <div className="error">{errors.firstName}</div> : null}
            </div>

            <div className="field">
              <label className="label" htmlFor="lastName">
                Last Name
              </label>
              <input
                id="lastName"
                name="lastName"
                className={`input ${touched.lastName && errors.lastName ? "inputError" : ""}`}
                value={form.lastName}
                onChange={onChange}
                onBlur={onBlur}
                placeholder="Örn: Yılmaz"
                autoComplete="family-name"
              />
              {touched.lastName && errors.lastName ? <div className="error">{errors.lastName}</div> : null}
            </div>
          </div>

          <div className="field">
            <label className="label" htmlFor="email">
              E-mail
            </label>
            <input
              id="email"
              name="email"
              type="email"
              className={`input ${touched.email && errors.email ? "inputError" : ""}`}
              value={form.email}
              onChange={onChange}
              onBlur={onBlur}
              placeholder="ornek@email.com"
              autoComplete="email"
            />
            {touched.email && errors.email ? <div className="error">{errors.email}</div> : null}
          </div>

          <div className="field">
            <label className="label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              name="password"
              type="password"
              className={`input ${touched.password && errors.password ? "inputError" : ""}`}
              value={form.password}
              onChange={onChange}
              onBlur={onBlur}
              placeholder="En az 8 karakter (harf + rakam)"
              autoComplete="new-password"
            />
            {touched.password && errors.password ? <div className="error">{errors.password}</div> : null}
          </div>

          <div className="field">
            <label className="label" htmlFor="gender">
              Gender
            </label>
            <select
              id="gender"
              name="gender"
              className={`select ${touched.gender && errors.gender ? "inputError" : ""}`}
              value={form.gender}
              onChange={onChange}
              onBlur={onBlur}
            >
              {GENDERS.map((g) => (
                <option key={g.value} value={g.value}>
                  {g.label}
                </option>
              ))}
            </select>
            {touched.gender && errors.gender ? <div className="error">{errors.gender}</div> : null}
          </div>

          <button className="primaryButton" type="submit" disabled={!canSubmit}>
            {isSubmitting ? "Kaydediliyor..." : "Create Account"}
          </button>

          <div className="bottomRow">
            <button type="button" className="ghostButton" onClick={onCancel}>
              Cancel
            </button>

            <span className="muted">
              Hesabın var mı?{" "}
              <Link className="link" to="/login">
                Login
              </Link>
            </span>
          </div>
        </form>
      </div>
    </div>
  );
}
