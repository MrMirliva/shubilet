// src/pages/customer/MyTicketsPage.jsx
import { useEffect, useMemo, useState } from "react";
import "./MyTicketsPage.css";

const API_PATH = "/api/tickets/get/customer"; // <-- BURAYI senin gerçek route'a göre güncelle

async function safeJson(res) {
  const text = await res.text();
  try {
    return text ? JSON.parse(text) : null;
  } catch {
    return { message: text || "Unexpected response." };
  }
}

function fmtDateTime(value) {
  if (!value) return "—";
  // backend string ise olduğu gibi gösterelim; ISO ise daha okunur yaparız
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return String(value);
  return d.toLocaleString();
}

export default function MyTicketsPage() {
  const [tickets, setTickets] = useState([]);
  const [query, setQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all"); // all | active | past
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchTickets();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchTickets = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await fetch(API_PATH, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      });

      const data = await safeJson(res);

      if (!res.ok) {
        setTickets([]);
        setError(data?.message || "Failed to load tickets.");
        return;
      }

      // Beklenen: { message: "Success", tickets: [...] }
      const list = data?.tickets || data?.ticketGetResults || data?.data || [];
      setTickets(Array.isArray(list) ? list : []);
    } catch {
      setError("Unable to reach the server.");
      setTickets([]);
    } finally {
      setLoading(false);
    }
  };

  const filteredTickets = useMemo(() => {
    const q = query.trim().toLowerCase();

    return tickets.filter((t) => {
      // olası alan adları:
      const company = (t.companyName ?? t.companyTitle ?? t.title ?? "").toLowerCase();
      const from = (t.fromCity ?? t.departureCity ?? t.from ?? t.origin ?? "").toLowerCase();
      const to = (t.toCity ?? t.arrivalCity ?? t.to ?? t.destination ?? "").toLowerCase();
      const pnr = String(t.pnr ?? t.ticketNo ?? t.ticketId ?? t.id ?? "").toLowerCase();

      const matchesQuery =
        !q || company.includes(q) || from.includes(q) || to.includes(q) || pnr.includes(q);

      if (!matchesQuery) return false;

      if (statusFilter === "all") return true;

      // tarih alanı tahmini (backend DTO’ya göre sonra netleştiririz)
      const dtValue = t.departureTime ?? t.dateTime ?? t.departureDate ?? t.date ?? null;
      const d = dtValue ? new Date(dtValue) : null;
      const isPast = d && !Number.isNaN(d.getTime()) ? d.getTime() < Date.now() : false;

      if (statusFilter === "active") return !isPast;
      if (statusFilter === "past") return isPast;

      return true;
    });
  }, [tickets, query, statusFilter]);

  return (
    <div className="myTicketsPage withBusBg">
      <div className="myTicketsCard">
        <div className="headerRow">
          <div>
            <h1 className="title">My Tickets</h1>
            <p className="subtitle">
              View and manage your purchased tickets.
            </p>
          </div>

          <button className="refreshBtn" type="button" onClick={fetchTickets} disabled={loading}>
            Refresh
          </button>
        </div>

        <div className="controls">
          <div className="searchWrap">
            <label className="label" htmlFor="q">Search</label>
            <input
              id="q"
              className="input"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search by company, route, or ticket number..."
            />
          </div>

          <div className="filterWrap">
            <label className="label" htmlFor="status">Filter</label>
            <select
              id="status"
              className="select"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="all">All</option>
              <option value="active">Upcoming</option>
              <option value="past">Past</option>
            </select>
          </div>
        </div>

        {loading && <p className="info">Loading tickets...</p>}
        {error && <p className="error">{error}</p>}

        {!loading && !error && filteredTickets.length === 0 && (
          <div className="empty">
            <div className="emptyTitle">No tickets found</div>
            <div className="emptyText">
              Try changing the filter or search query.
            </div>
          </div>
        )}

        {!loading && !error && filteredTickets.length > 0 && (
          <div className="tableWrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Ticket</th>
                  <th>Company</th>
                  <th>Route</th>
                  <th>Departure</th>
                  <th className="right">Seat</th>
                  <th className="right">Price</th>
                </tr>
              </thead>
              <tbody>
                {filteredTickets.map((t, idx) => {
                  const ticketNo = t.pnr ?? t.ticketNo ?? t.ticketId ?? t.id ?? `#${idx + 1}`;
                  const company = t.companyName ?? t.companyTitle ?? "—";
                  const from = t.fromCity ?? t.departureCity ?? t.from ?? t.origin ?? "—";
                  const to = t.toCity ?? t.arrivalCity ?? t.to ?? t.destination ?? "—";
                  const departure = fmtDateTime(t.departureTime ?? t.dateTime ?? t.departureDate ?? t.date);
                  const seat = t.seatNo ?? t.seatNumber ?? "—";
                  const price = t.price ?? t.amount ?? null;
                  const currency = t.currency ?? "₺";

                  return (
                    <tr key={t.id ?? t.ticketId ?? `${ticketNo}-${idx}`}>
                      <td className="mono">{ticketNo}</td>
                      <td>{company}</td>
                      <td>{from} → {to}</td>
                      <td>{departure}</td>
                      <td className="right">{seat}</td>
                      <td className="right">{price != null ? `${price} ${currency}` : "—"}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        <p className="hint">
          Note: This page uses your session cookie. If you are logged out, the server will return an error.
        </p>
      </div>
    </div>
  );
}
