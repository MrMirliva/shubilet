// src/App.jsx

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/login/LoginPage.jsx';

// İleride başka sayfalar da ekleyeceğiz (Register, Home vb.)
// Şimdilik sadece Login var.

function App() {
  return (
    <Router>
      <Routes>
        {/* Ana sayfaya girince direkt Login açılsın diye path="/" verdim */}
        <Route path="/" element={<LoginPage />} />
        
        {/* "/login" yazınca da açılsın */}
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </Router>
  );
}

export default App;