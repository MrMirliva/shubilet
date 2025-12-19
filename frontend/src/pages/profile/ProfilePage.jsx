// src/pages/profile/ProfilePage.jsx
import { useMemo, useState } from "react";
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

export default function ProfilePage() {
  // --- PROFILE STATE ---
  const [profile, setProfile] = useState(mockProfile);
  const [draft, setDraft] = useState(mockProfile);
  const [isEditing, setIsEditing] = useState(false);

  // --- PASSWORD MODAL ---
  const [pwOpen, setPwOpen] = useState(false);
  const [pwForm, setPwForm] = useState({ current: "", next: "", confirm: "" });
  const [pwTouched, setPwTouched] = useState({ current: false, next: false, confirm: false });

  // --- CARD STATE ---
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

  // --- UI MESSAGE (NO API) ---
  const [toast, setToast] = useState("");

  function showToast(msg) {
    setToast(msg);
    window.clearTimeout(showToast._t);
    showToast._t = window.setTimeout(() => setToast(""), 2200);
  }

  // --------- PROFILE VALIDATION ----------
  const profileErrors = useMemo(() => {
    const e = {};
    const name = draft.name?.trim() ?? "";
    const surname = draft.surname?.trim() ?? "";
    const email = draft.email?.trim() ?? "";
    const gender = draft.gender ?? "";

    if (!name) e.name = "Name is required.";
    if (!surname) e.surname = "Surname is required.";
    if (!gender) e.gender = "Gender is required.";
    if (!email) e.email = "Email is required.";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) e.email = "Please enter a valid email.";

    return e;
  }, [draft]);

  const profileHasError = Object.keys(profileErrors).length > 0;

  function onEdit() {
    setDraft(profile);
    setIsEditing(true);
  }

  function onCancelEdit() {
    setDraft(profile);
    setIsEditing(false);
  }

  function onSaveProfile() {
    // no API: just local save
    if (profileHasError) return;
    setProfile(draft);
    setIsEditing(false);
    showToast("Profile updated (mock).");
  }

  // --------- PASSWORD VALIDATION ----------
  const pwErrors = useMemo(() => {
    const e = {};
    const current = pwForm.current;
    const next = pwForm.next;
    const confirm = pwForm.confirm;

    if (!current) e.current = "Current password is required.";
    if (!next) e.next = "New password is required.";
    else if (next.length < 8) e.next = "New password must be at least 8 characters.";
    if (!confirm) e.confirm = "Please confirm the new password.";
    else if (confirm !== next) e.confirm = "Passwords do not match.";

    return e;
  }, [pwForm]);

  const pwHasError = Object.keys(pwErrors).length > 0;

  function openPassword() {
    setPwForm({ current: "", next: "", confirm: "" });
    setPwTouched({ current: false, next: false, confirm: false });
    setPwOpen(true);
  }

  function closePassword() {
    setPwOpen(false);
  }

  function onSavePassword() {
    if (pwHasError) return;
    // no API: just mock success
    setPwOpen(false);
    showToast("Password changed (mock).");
  }

  // --------- CARD VALIDATION ----------
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
          </div>

          {toast ? <div className="toast" role="status">{toast}</div> : null}
        </header>

        {/* PROFILE CARD */}
        <section className="card">
          <div className="cardTop">
            <div className="cardTitleWrap">
              <h2 className="cardTitle">Profile Information</h2>
              <span className="pill">Account</span>
            </div>

            {!isEditing ? (
              <button className="ghostButton" type="button" onClick={onEdit}>
                Edit
              </button>
            ) : (
              <div className="rowActions">
                <button
                  className="primaryButton"
                  type="button"
                  onClick={onSaveProfile}
                  disabled={profileHasError}
                  title={profileHasError ? "Fix validation errors" : "Save"}
                >
                  Save
                </button>
                <button className="ghostButton" type="button" onClick={onCancelEdit}>
                  Cancel
                </button>
              </div>
            )}
          </div>

          <div className="grid">
            <Field
              label="Name"
              value={draft.name}
              readOnly={!isEditing}
              error={isEditing ? profileErrors.name : ""}
              onChange={(v) => setDraft((p) => ({ ...p, name: v }))}
            />
            <Field
              label="Surname"
              value={draft.surname}
              readOnly={!isEditing}
              error={isEditing ? profileErrors.surname : ""}
              onChange={(v) => setDraft((p) => ({ ...p, surname: v }))}
            />

            <div className="field">
              <label className="label">Gender</label>
              {!isEditing ? (
                <div className="valueBox">{profile.gender}</div>
              ) : (
                <>
                  <select
                    className={`input ${profileErrors.gender ? "inputError" : ""}`}
                    value={draft.gender}
                    onChange={(e) => setDraft((p) => ({ ...p, gender: e.target.value }))}
                  >
                    <option value="">Select</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                  {profileErrors.gender ? <p className="error">{profileErrors.gender}</p> : null}
                </>
              )}
            </div>

            <Field
              label="Email"
              value={draft.email}
              readOnly={!isEditing}
              error={isEditing ? profileErrors.email : ""}
              onChange={(v) => setDraft((p) => ({ ...p, email: v }))}
            />

            <div className="field span2">
              <label className="label">Password</label>
              <div className="passwordRow">
                <div className="valueBox">********</div>
                <button className="ghostButton" type="button" onClick={openPassword}>
                  Change Password
                </button>
              </div>
              <p className="hint">
                For security reasons, your password is never displayed.
              </p>
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

      {/* PASSWORD MODAL */}
      {pwOpen ? (
        <Modal title="Change Password" onClose={closePassword}>
          <div className="modalBody">
            <ModalField
              label="Current Password"
              type="password"
              value={pwForm.current}
              error={pwTouched.current ? pwErrors.current : ""}
              onBlur={() => setPwTouched((t) => ({ ...t, current: true }))}
              onChange={(v) => setPwForm((p) => ({ ...p, current: v }))}
            />
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
            <button className="primaryButton" type="button" onClick={onSavePassword} disabled={pwHasError}>
              Save
            </button>
            <button className="ghostButton" type="button" onClick={closePassword}>
              Cancel
            </button>
          </div>
        </Modal>
      ) : null}

      {/* ADD CARD MODAL */}
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

            <p className="hint">
              This is a mock form. In real flow, never store full card data on frontend.
            </p>
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

function Field({ label, value, readOnly, onChange, error }) {
  return (
    <div className="field">
      <label className="label">{label}</label>
      {!readOnly ? (
        <>
          <input
            className={`input ${error ? "inputError" : ""}`}
            value={value ?? ""}
            onChange={(e) => onChange(e.target.value)}
          />
          {error ? <p className="error">{error}</p> : null}
        </>
      ) : (
        <div className="valueBox">{value || "-"}</div>
      )}
    </div>
  );
}

function ModalField({ label, value, onChange, onBlur, error, type = "text", placeholder }) {
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
          <button className="iconButton" type="button" onClick={onClose} aria-label="Close">
            ✕
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}
