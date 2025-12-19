// src/pages/profile/ProfilePage.jsx
import { useEffect, useMemo, useState } from "react";
import "./ProfilePage.css";

const mockProfile = {
  name: "Abdullah",
  surname: "Gündüz",
  gender: "Male", // "Male" | "Female" | "Other"
  email: "mirliva@example.com",
};

const mockCards = [
  { cardId: "1", last4Digits: "0037", expirationMonth: "11", expirationYear: "35" },
  { cardId: "2", last4Digits: "4412", expirationMonth: "08", expirationYear: "29" },
];

const FIELD = {
  NAME: "name",
  SURNAME: "surname",
  GENDER: "gender",
  EMAIL: "email",
  PASSWORD: "password",
};

async function safeReadJson(res) {
  try {
    return await res.json();
  } catch {
    return null;
  }
}

function buildEditEndpoint(field) {
  // field values match API suffix: name | surname | gender | email | password
  return `/api/profile/customer/edit/${field}`;
}

async function updateProfileAttribute(field, attributeValue) {
  const endpoint = buildEditEndpoint(field);

  const res = await fetch(endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ attribute: attributeValue }),
  });

  const data = await safeReadJson(res);

  if (!res.ok) {
    throw new Error(data?.message || `Update failed (HTTP ${res.status}).`);
  }

  return data; // MessageDTO
}

export default function ProfilePage() {
  // --- PROFILE STATE ---
  const [profile, setProfile] = useState(mockProfile);
  const [isProfileLoading, setIsProfileLoading] = useState(true);
  const [profileError, setProfileError] = useState("");

  // --- SINGLE "FIELD CHANGE" MODAL STATE ---
  const [fieldModal, setFieldModal] = useState({
    open: false,
    field: null, // FIELD.NAME | FIELD.SURNAME | FIELD.GENDER | FIELD.EMAIL | FIELD.PASSWORD
  });

  // --- FOR NON-PASSWORD FIELDS ---
  const [fieldValue, setFieldValue] = useState(""); // input value
  const [fieldTouched, setFieldTouched] = useState(false);

  // --- PASSWORD MODAL FORM (NO CURRENT PASSWORD) ---
  const [pwForm, setPwForm] = useState({ next: "", confirm: "" });
  const [pwTouched, setPwTouched] = useState({ next: false, confirm: false });

  // --- FIELD SAVE LOADING ---
  const [isSaving, setIsSaving] = useState(false);

  // --- CARD STATE (unchanged) ---
  const [cards, setCards] = useState(mockCards);
  const [cardModalOpen, setCardModalOpen] = useState(false);
  const [cardForm, setCardForm] = useState({
    cardHolderName: "",
    cardNumber: "",
    expirationMonth: "",
    expirationYear: "",
    cvc: "",
  });
  const [cardTouched, setCardTouched] = useState({});

  // --- UI MESSAGE ---
  const [toast, setToast] = useState("");

  function showToast(msg) {
    setToast(msg);
    window.clearTimeout(showToast._t);
    showToast._t = window.setTimeout(() => setToast(""), 2200);
  }

  // ---------------- LOAD PROFILE (API) ----------------
  useEffect(() => {
    let alive = true;

    async function loadProfile() {
      setIsProfileLoading(true);
      setProfileError("");

      try {
        const res = await fetch("/api/profile/customer/get", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({}),
        });

        const data = await safeReadJson(res);

        if (!res.ok) {
          const msg = data?.message || `Profile fetch failed (HTTP ${res.status}).`;
          if (alive) setProfileError(msg);
          return;
        }

        const nextProfile = {
          name: data?.name ?? "",
          surname: data?.surname ?? "",
          gender: data?.gender ?? "",
          email: data?.email ?? "",
        };

        if (alive) {
          setProfile(nextProfile);
          if (data?.message) showToast(data.message);
        }
      } catch {
        if (alive) setProfileError("Profile fetch failed. Please try again.");
      } finally {
        if (alive) setIsProfileLoading(false);
      }
    }

    loadProfile();
    return () => {
      alive = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ---------------- FIELD MODAL HELPERS ----------------
  function openFieldModal(field) {
    if (isProfileLoading) return;

    setFieldModal({ open: true, field });
    setFieldTouched(false);

    if (field === FIELD.PASSWORD) {
      setPwForm({ next: "", confirm: "" });
      setPwTouched({ next: false, confirm: false });
    } else {
      setFieldValue(String(profile[field] ?? ""));
    }
  }

  function closeFieldModal() {
    if (isSaving) return;
    setFieldModal({ open: false, field: null });
  }

  const activeField = fieldModal.field;

  const fieldTitle = useMemo(() => {
    switch (activeField) {
      case FIELD.NAME:
        return "Change Name";
      case FIELD.SURNAME:
        return "Change Surname";
      case FIELD.GENDER:
        return "Change Gender";
      case FIELD.EMAIL:
        return "Change Email";
      case FIELD.PASSWORD:
        return "Change Password";
      default:
        return "";
    }
  }, [activeField]);

  // ---------------- VALIDATIONS ----------------
  const fieldError = useMemo(() => {
    if (!fieldModal.open) return "";
    if (!activeField || activeField === FIELD.PASSWORD) return "";

    const v = fieldValue.trim();

    if (activeField === FIELD.NAME) {
      if (!v) return "Name is required.";
      if (v.length < 2) return "Name must be at least 2 characters.";
      return "";
    }

    if (activeField === FIELD.SURNAME) {
      if (!v) return "Surname is required.";
      if (v.length < 2) return "Surname must be at least 2 characters.";
      return "";
    }

    if (activeField === FIELD.EMAIL) {
      if (!v) return "Email is required.";
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) return "Please enter a valid email.";
      return "";
    }

    if (activeField === FIELD.GENDER) {
      if (!v) return "Gender is required.";
      return "";
    }

    return "";
  }, [fieldModal.open, activeField, fieldValue]);

  const pwErrors = useMemo(() => {
    const e = {};
    if (!fieldModal.open || activeField !== FIELD.PASSWORD) return e;

    const next = pwForm.next;
    const confirm = pwForm.confirm;

    if (!next) e.next = "New password is required.";
    else if (next.length < 8) e.next = "New password must be at least 8 characters.";

    if (!confirm) e.confirm = "Please confirm the new password.";
    else if (confirm !== next) e.confirm = "Passwords do not match.";

    return e;
  }, [fieldModal.open, activeField, pwForm]);

  const canSaveField =
    !!activeField &&
    activeField !== FIELD.PASSWORD &&
    fieldValue.trim().length > 0 &&
    !fieldError;

  const canSavePassword = activeField === FIELD.PASSWORD && Object.keys(pwErrors).length === 0;

  // ---------------- SAVE HANDLERS (API) ----------------
  async function onSaveFieldChange() {
    if (!canSaveField || isSaving) return;

    const v = fieldValue.trim();

    setIsSaving(true);
    try {
      const data = await updateProfileAttribute(activeField, v);

      setProfile((p) => ({ ...p, [activeField]: v }));
      setFieldModal({ open: false, field: null });

      showToast(data?.message || "Updated successfully.");
    } catch (err) {
      showToast(err?.message || "Update failed.");
    } finally {
      setIsSaving(false);
    }
  }

  async function onSavePasswordChange() {
    if (!canSavePassword || isSaving) return;

    setIsSaving(true);
    try {
      // password endpoint expects { attribute: <newPassword> }
      const data = await updateProfileAttribute(FIELD.PASSWORD, pwForm.next);

      setFieldModal({ open: false, field: null });
      showToast(data?.message || "Password changed successfully.");
    } catch (err) {
      showToast(err?.message || "Password update failed.");
    } finally {
      setIsSaving(false);
    }
  }

  // ---------------- CARD HANDLERS (SAME / MOCK) ----------------
  function onlyDigits(s) {
    return (s ?? "").replace(/\D/g, "");
  }

  const cardErrors = useMemo(() => {
    const e = {};
    const holder = cardForm.cardHolderName.trim();
    const num = onlyDigits(cardForm.cardNumber);
    const mm = cardForm.expirationMonth.trim();
    const yy = cardForm.expirationYear.trim();
    const cvc = onlyDigits(cardForm.cvc);

    if (!holder) e.cardHolderName = "Card holder name is required.";
    if (!num) e.cardNumber = "Card number is required.";
    else if (num.length !== 16) e.cardNumber = "Card number must be 16 digits.";

    if (!mm) e.expirationMonth = "Month is required.";
    else if (!/^(0[1-9]|1[0-2])$/.test(mm)) e.expirationMonth = "Month must be 01-12.";

    if (!yy) e.expirationYear = "Year is required.";
    else if (!/^\d{2}$/.test(yy)) e.expirationYear = "Year must be 2 digits (e.g. 29).";

    if (!cvc) e.cvc = "CVC is required.";
    else if (cvc.length !== 3) e.cvc = "CVC must be 3 digits.";

    return e;
  }, [cardForm]);

  const cardHasError = Object.keys(cardErrors).length > 0;

  function openCardModal() {
    setCardForm({
      cardHolderName: "",
      cardNumber: "",
      expirationMonth: "",
      expirationYear: "",
      cvc: "",
    });
    setCardTouched({});
    setCardModalOpen(true);
  }

  function closeCardModal() {
    setCardModalOpen(false);
  }

  function onAddCard() {
    if (cardHasError) return;

    const num = onlyDigits(cardForm.cardNumber);
    const last4 = num.slice(-4);

    const newCard = {
      cardId: String(Date.now()),
      last4Digits: last4,
      expirationMonth: cardForm.expirationMonth,
      expirationYear: cardForm.expirationYear,
    };

    setCards((prev) => [newCard, ...prev]);
    setCardModalOpen(false);
    showToast("Card added (mock).");
  }

  function onRemoveCard(cardId) {
    const ok = window.confirm("Remove this card?");
    if (!ok) return;
    setCards((prev) => prev.filter((c) => c.cardId !== cardId));
    showToast("Card removed (mock).");
  }

  return (
    <div className="profilePage">
      <div className="profileShell">
        <header className="profileHeader">
          <div>
            <h1 className="title">My Profile</h1>
            <p className="subtitle">Manage your account information and saved cards</p>

            {profileError ? (
              <div className="errorBanner" role="alert">
                <div>{profileError}</div>
                <button className="ghostButton" type="button" onClick={() => window.location.reload()}>
                  Retry
                </button>
              </div>
            ) : null}
          </div>

          {toast ? (
            <div className="toast" role="status">
              {toast}
            </div>
          ) : null}
        </header>

        {/* PROFILE CARD */}
        <section className="card">
          <div className="cardTop">
            <div className="cardTitleWrap">
              <h2 className="cardTitle">Profile Information</h2>
              <span className="pill">Account</span>
            </div>
          </div>

          <div className="grid">
            <InlineRow
              label="Name"
              value={isProfileLoading ? "Loading..." : profile.name}
              buttonText="Change"
              disabled={isProfileLoading}
              onClick={() => openFieldModal(FIELD.NAME)}
            />
            <InlineRow
              label="Surname"
              value={isProfileLoading ? "Loading..." : profile.surname}
              buttonText="Change"
              disabled={isProfileLoading}
              onClick={() => openFieldModal(FIELD.SURNAME)}
            />
            <InlineRow
              label="Gender"
              value={isProfileLoading ? "Loading..." : profile.gender}
              buttonText="Change"
              disabled={isProfileLoading}
              onClick={() => openFieldModal(FIELD.GENDER)}
            />
            <InlineRow
              label="Email"
              value={isProfileLoading ? "Loading..." : profile.email}
              buttonText="Change"
              disabled={isProfileLoading}
              onClick={() => openFieldModal(FIELD.EMAIL)}
            />

            <div className="field span2">
              <label className="label">Password</label>
              <div className="passwordRow">
                <div className="valueBox">********</div>
                <button
                  className="ghostButton"
                  type="button"
                  disabled={isProfileLoading}
                  onClick={() => openFieldModal(FIELD.PASSWORD)}
                >
                  Change
                </button>
              </div>
              <p className="hint">For security reasons, your password is never displayed.</p>
            </div>
          </div>
        </section>

        {/* CARDS CARD */}
        <section className="card">
          <div className="cardTop">
            <div className="cardTitleWrap">
              <h2 className="cardTitle">Saved Cards</h2>
              <span className="pill">Payment</span>
            </div>
            <button className="primaryButton" type="button" onClick={openCardModal}>
              Add Card
            </button>
          </div>

          {cards.length === 0 ? (
            <div className="emptyState">
              <p className="emptyTitle">No saved cards</p>
              <p className="emptyDesc">Add a card to speed up your purchases.</p>
              <button className="primaryButton" type="button" onClick={openCardModal}>
                Add your first card
              </button>
            </div>
          ) : (
            <div className="cardsList">
              {cards.map((c) => (
                <div key={c.cardId} className="cardRow">
                  <div className="cardMeta">
                    <div className="cardBadge">CARD</div>
                    <div className="cardInfo">
                      <div className="cardLine">
                        **** **** **** <b>{c.last4Digits}</b>
                      </div>
                      <div className="cardSub">
                        Expires {c.expirationMonth}/{c.expirationYear}
                      </div>
                    </div>
                  </div>

                  <button
                    className="dangerButton"
                    type="button"
                    onClick={() => onRemoveCard(c.cardId)}
                    aria-label={`Remove card ending with ${c.last4Digits}`}
                  >
                    Remove
                  </button>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>

      {/* FIELD MODAL (NAME/SURNAME/GENDER/EMAIL/PASSWORD) */}
      {fieldModal.open ? (
        <Modal title={fieldTitle} onClose={closeFieldModal}>
          {activeField === FIELD.PASSWORD ? (
            <>
              <div className="modalBody">
                <ModalField
                  label="New Password"
                  type="password"
                  value={pwForm.next}
                  error={pwTouched.next ? pwErrors.next : ""}
                  onBlur={() => setPwTouched((t) => ({ ...t, next: true }))}
                  onChange={(v) => setPwForm((p) => ({ ...p, next: v }))}
                />
                <ModalField
                  label="Confirm New Password"
                  type="password"
                  value={pwForm.confirm}
                  error={pwTouched.confirm ? pwErrors.confirm : ""}
                  onBlur={() => setPwTouched((t) => ({ ...t, confirm: true }))}
                  onChange={(v) => setPwForm((p) => ({ ...p, confirm: v }))}
                />
              </div>

              <div className="modalActions">
                <button
                  className="primaryButton"
                  type="button"
                  onClick={onSavePasswordChange}
                  disabled={!canSavePassword || isSaving}
                >
                  {isSaving ? "Saving..." : "Save"}
                </button>
                <button className="ghostButton" type="button" onClick={closeFieldModal} disabled={isSaving}>
                  Cancel
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="modalBody">
                {activeField === FIELD.GENDER ? (
                  <div className="field">
                    <label className="label">Gender</label>
                    <select
                      className={`input ${fieldTouched && fieldError ? "inputError" : ""}`}
                      value={fieldValue}
                      onBlur={() => setFieldTouched(true)}
                      onChange={(e) => setFieldValue(e.target.value)}
                      disabled={isSaving}
                    >
                      <option value="">Select</option>
                      <option value="Male">Male</option>
                      <option value="Female">Female</option>
                      <option value="Other">Other</option>
                    </select>
                    {fieldTouched && fieldError ? <p className="error">{fieldError}</p> : null}
                  </div>
                ) : (
                  <ModalField
                    label={activeField === FIELD.NAME ? "Name" : activeField === FIELD.SURNAME ? "Surname" : "Email"}
                    value={fieldValue}
                    error={fieldTouched ? fieldError : ""}
                    onBlur={() => setFieldTouched(true)}
                    onChange={(v) => setFieldValue(v)}
                    disabled={isSaving}
                  />
                )}
              </div>

              <div className="modalActions">
                <button
                  className="primaryButton"
                  type="button"
                  onClick={onSaveFieldChange}
                  disabled={!canSaveField || isSaving}
                >
                  {isSaving ? "Saving..." : "Save"}
                </button>
                <button className="ghostButton" type="button" onClick={closeFieldModal} disabled={isSaving}>
                  Cancel
                </button>
              </div>
            </>
          )}
        </Modal>
      ) : null}

      {/* ADD CARD MODAL (unchanged) */}
      {cardModalOpen ? (
        <Modal title="Add Card" onClose={closeCardModal}>
          <div className="modalBody">
            <ModalField
              label="Card Holder Name"
              value={cardForm.cardHolderName}
              error={cardTouched.cardHolderName ? cardErrors.cardHolderName : ""}
              onBlur={() => setCardTouched((t) => ({ ...t, cardHolderName: true }))}
              onChange={(v) => setCardForm((p) => ({ ...p, cardHolderName: v }))}
            />

            <ModalField
              label="Card Number"
              value={cardForm.cardNumber}
              error={cardTouched.cardNumber ? cardErrors.cardNumber : ""}
              onBlur={() => setCardTouched((t) => ({ ...t, cardNumber: true }))}
              onChange={(v) => setCardForm((p) => ({ ...p, cardNumber: v }))}
              placeholder="16 digits"
            />

            <div className="modalGrid">
              <ModalField
                label="Exp. Month"
                value={cardForm.expirationMonth}
                error={cardTouched.expirationMonth ? cardErrors.expirationMonth : ""}
                onBlur={() => setCardTouched((t) => ({ ...t, expirationMonth: true }))}
                onChange={(v) => setCardForm((p) => ({ ...p, expirationMonth: v }))}
                placeholder="MM"
              />
              <ModalField
                label="Exp. Year"
                value={cardForm.expirationYear}
                error={cardTouched.expirationYear ? cardErrors.expirationYear : ""}
                onBlur={() => setCardTouched((t) => ({ ...t, expirationYear: true }))}
                onChange={(v) => setCardForm((p) => ({ ...p, expirationYear: v }))}
                placeholder="YY"
              />
            </div>

            <ModalField
              label="CVC"
              value={cardForm.cvc}
              error={cardTouched.cvc ? cardErrors.cvc : ""}
              onBlur={() => setCardTouched((t) => ({ ...t, cvc: true }))}
              onChange={(v) => setCardForm((p) => ({ ...p, cvc: v }))}
              placeholder="3 digits"
            />

            <p className="hint">This is a mock form. In real flow, never store full card data on frontend.</p>
          </div>

          <div className="modalActions">
            <button className="primaryButton" type="button" onClick={onAddCard} disabled={cardHasError}>
              Save
            </button>
            <button className="ghostButton" type="button" onClick={closeCardModal}>
              Cancel
            </button>
          </div>
        </Modal>
      ) : null}
    </div>
  );
}

function InlineRow({ label, value, buttonText = "Change", onClick, disabled = false }) {
  return (
    <div className="field">
      <label className="label">{label}</label>
      <div className="inlineRow">
        <div className="valueBox">{value || "-"}</div>
        <button className="ghostButton" type="button" onClick={onClick} disabled={disabled}>
          {buttonText}
        </button>
      </div>
    </div>
  );
}

function ModalField({ label, value, onChange, onBlur, error, type = "text", placeholder, disabled = false }) {
  return (
    <div className="field">
      <label className="label">{label}</label>
      <input
        className={`input ${error ? "inputError" : ""}`}
        type={type}
        value={value ?? ""}
        placeholder={placeholder}
        onBlur={onBlur}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
      />
      {error ? <p className="error">{error}</p> : null}
    </div>
  );
}

function Modal({ title, onClose, children }) {
  return (
    <div className="modalOverlay" role="dialog" aria-modal="true" aria-label={title}>
      <div className="modalCard">
        <div className="modalTop">
          <h3 className="modalTitle">{title}</h3>
          <button className="iconButton" type="button" onClick={onClose} aria-label="Close" disabled={false}>
            ✕
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}
