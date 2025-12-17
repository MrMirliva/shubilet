// src/App.jsx

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from "./components/Header/Header.jsx";
import LoginPage from './pages/login/LoginPage.jsx';
import RegisterPage from "./pages/register/RegisterPage.jsx";
import CustomerRegister from "./pages/registerCustomer/CustomerRegister.jsx";
import CompanyRegister from "./pages/registerCompany/CompanyRegister.jsx";
import AdminRegister from "./pages/registerAdmin/AdminRegister.jsx";


// İleride başka sayfalar da ekleyeceğiz (Register, Home vb.)
// Şimdilik sadece Login var.

function App() {
  return (

    <Router>
      <Header />
      <Routes>
        {/* Ana sayfaya girince direkt Login açılsın diye path="/" verdim */}
        <Route path="/" element={<LoginPage />} />
        
        {/* "/login" yazınca da açılsın */}
        <Route path="/login" element={<LoginPage />} />

        <Route path="/register" element={<RegisterPage />} />
        <Route path="/register/customer" element={<CustomerRegister />} />
        <Route path="/register/company" element={<CompanyRegister />} />
        <Route path="/register/admin" element={<AdminRegister />} />
      </Routes>
    </Router>
  );
}

export default App;