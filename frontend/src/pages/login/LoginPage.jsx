// src/pages/Login/LoginPage.jsx

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Yönlendirme için (react-router-dom kurulu varsayıyorum)
import './LoginPage.css';

const LoginPage = () => {
    const navigate = useNavigate();

    // State Yönetimi
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    // Input değişimi
    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        // Kullanıcı yazmaya başlayınca hatayı sil
        if (error) setError(null);
    };

    // Form Gönderimi (Use Case 4: Main Flow)
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Temel Validasyon (Use Case 4: Alternate Flow 2a)
        if (!formData.email || !formData.password) {
            setError("Lütfen e-posta ve şifre alanlarını doldurunuz.");
            return;
        }

        setIsLoading(true);

        try {
            // --- BACKEND BAĞLANTISI (SİMÜLASYON) ---
            // Gerçekte burası: const response = await AuthService.login(formData);
            
            const response = await mockLoginProcess(formData.email, formData.password);

            if (response.success) {
                // Token'ı kaydet (LocalStorage veya Context)
                localStorage.setItem('token', response.token);
                localStorage.setItem('userRole', response.role);

                // Rol bazlı yönlendirme (Use Case 4: Step 5)
                switch(response.role) {
                    case 'ADMIN':
                        navigate('/admin-panel');
                        break;
                    case 'COMPANY':
                        navigate('/company-panel');
                        break;
                    case 'CUSTOMER':
                    default:
                        navigate('/'); // Anasayfa
                        break;
                }
            } else {
                // Hata Mesajı (Use Case 4: Alternate Flows 3b, 3c)
                setError(response.message);
            }

        } catch (err) {
            setError("Sunucuya bağlanırken bir hata oluştu. Lütfen tekrar deneyin.");
        } finally {
            setIsLoading(false);
        }
    };

    // --- MOCK LOGIN FONKSİYONU (Backend hazır olana kadar test için) ---
    const mockLoginProcess = (email, password) => {
        return new Promise((resolve) => {
            setTimeout(() => {
                // Basit bir test senaryosu
                if (email === "admin@shubilet.com" && password === "123456") {
                    resolve({ success: true, role: 'ADMIN', token: 'fake-jwt-token' });
                } else if (email === "user@shubilet.com" && password === "123456") {
                    resolve({ success: true, role: 'CUSTOMER', token: 'fake-jwt-token' });
                } else {
                    resolve({ success: false, message: "E-posta veya şifre hatalı." });
                }
            }, 1000); // 1 saniye bekleme süresi
        });
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <h2>Shu<span className="brand-highlight">Bilet</span></h2>
                    <p>Yolculuğunuza başlamak için giriş yapın</p>
                </div>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="email">E-posta Adresi</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            className="form-control"
                            placeholder="ornek@email.com"
                            value={formData.email}
                            onChange={handleChange}
                            disabled={isLoading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Şifre</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            className="form-control"
                            placeholder="********"
                            value={formData.password}
                            onChange={handleChange}
                            disabled={isLoading}
                        />
                    </div>

                    <button type="submit" className="login-btn" disabled={isLoading}>
                        {isLoading ? 'Giriş Yapılıyor...' : 'Giriş Yap'}
                    </button>
                </form>

                <div className="signup-link">
                    Hesabınız yok mu? <a href="/register">Hemen Kayıt Olun</a>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;