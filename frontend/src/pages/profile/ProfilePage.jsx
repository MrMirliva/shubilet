// src/pages/profile/ProfilePage.jsx
import { useEffect, useMemo, useState } from "react";
import "./ProfilePage.css";

const mockProfile = {
  name: "Abdullah",
  surname: "Gündüz",
  gender: "Male", // "Male" | "Female" | "Other"
  email: "mirliva@example.com",
};

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

function maskCardNo(cardNo) {
  const digits = String(cardNo ?? "").replace(/\D/g, "");
  const last4 = digits.slice(-4);
  if (!last4) return "****";
  return `**** **** **** ${last4}`;
}

async function fetchCards() {
  const res = await fetch("/api/profile/customer/get/cards", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({}), // request boş
  });

  const data = await safeReadJson(res);

  if (!res.ok) {
    throw new Error(data?.message || `Cards fetch failed (HTTP ${res.status}).`);
  }

  return data; // CardsDTO: { message, cards: [...] }
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

  // --- CARD STATE (API) ---
  const [cards, setCards] = useState([]); // CardDTO[]
  const [isCardsLoading, setIsCardsLoading] = useState(true);
  const [cardsError, setCardsError] = useState("");

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

  // ---------------- LOAD CARDS (API) ----------------
  useEffect(() => {
    let alive = true;

    async function loadCards() {
      setIsCardsLoading(true);
      setCardsError("");

      try {
        const data = await fetchCards();
        const list = Array.isArray(data?.cards) ? data.cards : [];

        if (alive) {
          setCards(list);
          if (data?.message) showToast(data.message);
        }
      } catch (err) {
        if (alive) setCardsError(err?.message || "Cards fetch failed. Please try again.");
      } finally {
        if (alive) setIsCardsLoading(false);
      }
    }

    loadCards();
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
      const data = await updateProfileAttribute(FIELD.PASSWORD, pwForm.next);

      setFieldModal({ open: false, field: null });
      showToast(data?.message || "Password changed successfully.");
    } catch (err) {
      showToast(err?.message || "Password update failed.");
    } finally {
      setIsSaving(false);
    }
  }

  // ---------------- CARD ACTIONS ----------------
  function onRemoveCard(cardId) {
    // Backend delete endpoint verilmediği için mock olarak listeden siliyoruz.
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

            {/* Backend add endpoint yok, şimdilik disable */}
            <button className="primaryButton" type="button" disabled title="Add Card endpoint not connected yet">
              Add Card
            </button>
          </div>

          {cardsError ? (
            <div className="errorBanner" role="alert">
              <div>{cardsError}</div>
              <button className="ghostButton" type="button" onClick={() => window.location.reload()}>
                Retry
              </button>
            </div>
          ) : isCardsLoading ? (
            <div className="emptyState">
              <p className="emptyTitle">Loading cards...</p>
              <p className="emptyDesc">Please wait.</p>
            </div>
          ) : cards.length === 0 ? (
            <div className="emptyState">
              <p className="emptyTitle">No saved cards</p>
              <p className="emptyDesc">You don't have any saved cards yet.</p>
            </div>
          ) : (
            <div className="cardsList">
              {cards.map((c) => (
                <div key={c.cardId} className="cardRow">
                  <div className="cardMeta">
                    <div className="cardBadge">CARD</div>
                    <div className="cardInfo">
                      <div className="cardLine">
                        <b>{maskCardNo(c.cardNo)}</b>
                      </div>
                      <div className="cardSub">
                        {c.name} {c.surname}
                      </div>
                    </div>
                  </div>

                  <button
                    className="dangerButton"
                    type="button"
                    onClick={() => onRemoveCard(c.cardId)}
                    aria-label={`Remove card id ${c.cardId}`}
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
