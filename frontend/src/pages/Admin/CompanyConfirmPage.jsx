// src/pages/admin/CompanyConfirmPage.jsx
import { useEffect, useState } from "react";
import "./CompanyConfirmPage.css";

export default function CompanyConfirmPage() {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // 👉 geçici olarak adminId sabit
  // login entegrasyonunda burası localStorage / session’dan alınacak
  const adminId = 1;

  useEffect(() => {
    fetchUnverifiedCompanies();
  }, []);

  const fetchUnverifiedCompanies = async () => {
    try {
      setLoading(true);
      const res = await fetch(
        "/api/verification/get/unverified/companies",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({ adminId }),
        }
      );

      const data = await res.json();

      if (!res.ok) {
        setError(data.message || "Failed to fetch companies.");
        return;
      }

      setCompanies(data.companies || []);
    } catch {
      setError("Server connection error.");
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async (companyId) => {
    try {
      const res = await fetch("/api/verification/verify/company", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          adminId,
          candidateCompanyId: companyId,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        alert(data.message || "Verification failed.");
        return;
      }

      // başarıyla onaylananı listeden düş
      setCompanies((prev) =>
        prev.filter((c) => c.id !== companyId)
      );
    } catch {
      alert("Server error during verification.");
    }
  };

  return (
    <div className="companyConfirmPage">
      <div className="companyConfirmCard">
        <h1 className="title">Company Verification</h1>
        <p className="subtitle">
          Review and approve registered companies.
        </p>

        {loading && <p className="info">Loading companies...</p>}
        {error && <p className="error">{error}</p>}

        {!loading && companies.length === 0 && (
          <p className="info">No companies awaiting verification.</p>
        )}

        {!loading && companies.length > 0 && (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Company Name</th>
                <th>Email</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {companies.map((company) => (
                <tr key={company.id}>
                  <td>{company.id}</td>
                  <td>{company.title}</td>
                  <td>{company.email}</td>
                  <td>
                    <button
                      className="approveBtn"
                      onClick={() => handleVerify(company.id)}
                    >
                      Approve
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
