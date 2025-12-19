// src/App.jsx

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import LoginPage from './pages/login/LoginPage.jsx';
import RegisterPage from "./pages/register/RegisterPage.jsx";
import CustomerRegister from "./pages/registerCustomer/CustomerRegister.jsx";
import CompanyRegister from "./pages/registerCompany/CompanyRegister.jsx";
import AdminRegister from "./pages/registerAdmin/AdminRegister.jsx";
import Travel from "./pages/travel/Travel.jsx";
import CustomerHomePage from './pages/customer/CustomerHomePage.jsx';
import AuthLayout from './layouts/AuthLayout.jsx';
import CustomerLayout from './layouts/CustomerLayout.jsx';
import AdminLayout from './layouts/AdminLayout.jsx';
import AdminHomePage from './pages/Admin/AdminHomePage.jsx';
import CompanyExpeditionDetail from './pages/companyExpeditionDetail/CompanyExpeditionDetail.jsx';
import CompanyExpeditionList from './pages/companyExpeditionList/CompanyExpeditionList.jsx';
import CompanyHome from './pages/companyHome/CompanyHome.jsx';
import CompanyExpeditionCreate from './pages/expeditionCreate/CompanyExpeditionCreate.jsx';
import CompanyConfirmPage from './pages/Admin/CompanyConfirmPage.jsx';
import AdminConfirmPage from './pages/Admin/AdminConfirmPage.jsx';

// İleride başka sayfalar da ekleyeceğiz (Register, Home vb.)
// Şimdilik sadece Login var.

function App() {
  return (

    <Router>
      <Routes>
        <Route element={<AuthLayout />}>
          {/* Auth ile ilgili sayfalar buraya */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/register/customer" element={<CustomerRegister />} />
          <Route path="/register/company" element={<CompanyRegister />} />
          <Route path="/register/admin" element={<AdminRegister />} />
        </Route>

        <Route element={<CustomerLayout />}>
          {/* Müşteri ile ilgili sayfalar buraya */}
          <Route path="/customer/home" element={<CustomerHomePage />} />
          <Route path="/travel" element={<Travel />} />
        </Route>

        <Route element={<AdminLayout />}>
          {/* Admin ile ilgili sayfalar buraya */}
          <Route path="/admin/home" element={<AdminHomePage />} />
          <Route path="/admin/confirmcompanies" element={<CompanyConfirmPage />} />
          <Route path="/admin/confirmadmins" element={<AdminConfirmPage />} />
          {/* Diğer admin sayfaları da buraya eklenecek */}
        </Route>

        {/* Ana sayfaya girince direkt Login açılsın diye path="/" verdim */}
        <Route path="/" element={<CustomerHomePage />} />

        {/* "/login" yazınca da açılsın */}

        <Route path="/travel" element={<Travel />} />

        {/* Şirket ile ilgili sayfalar buraya */}
        <Route path="/company" element={<CompanyHome />} />
        <Route path="/company/expeditions/create" element={<CompanyExpeditionCreate />} />
        <Route path="/company/expeditions" element={<CompanyExpeditionList />} />
        <Route path="/company/expeditions/:id" element={<CompanyExpeditionDetail />} />
      </Routes>
    </Router>
  );
}

export default App;