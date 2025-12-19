import React, { useState } from 'react';
import './Travel.css';

const CITIES = [
    "Adana", "Adıyaman", "Afyonkarahisar", " Ağrı", "Aksaray", "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin", "Aydın", "Balikesir", "Bartın", "Batman", "Bayburt", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankiri", "Çorum", "Denizli", "Diyarbakir", "Duzce", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul", "İzmir", "Kahramanmaras", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kirikkale", "Kirklareli", "Kirşehir", "Kilis", "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Mugla", "Mus", "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Usak", "Van", "Yalova", "Yozgat", "Zonguldak"
];

const CitySearchInput = ({ value, onChange, placeholder }) => {
    const [suggestions, setSuggestions] = useState([]);
    const [isFocused, setIsFocused] = useState(false);

    const handleInputChange = (e) => {
        const inputVal = e.target.value;
        onChange(inputVal);

        if (inputVal.length > 0) {
            const filtered = CITIES.filter(city =>
                city.toLocaleLowerCase('tr-TR').startsWith(inputVal.toLocaleLowerCase('tr-TR'))
            );
            setSuggestions(filtered);
        } else {
            setSuggestions([]);
        }
    };

    const handleSelectCity = (city) => {
        onChange(city);
        setSuggestions([]);
        setIsFocused(false);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            if (suggestions.length > 0) {
                // Select the first (top) suggestion
                handleSelectCity(suggestions[0]);
                e.preventDefault();
            }
        }
    };

    // Close suggestions after short delay to allow click event to register
    const handleBlur = () => {
        setTimeout(() => setIsFocused(false), 200);
    };

    return (
        <div className="city-autocomplete-wrapper">
            <input
                type="text"
                value={value}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
                onFocus={() => setIsFocused(true)}
                onBlur={handleBlur}
                placeholder={placeholder}
            />
            {isFocused && suggestions.length > 0 && (
                <div className="suggestions-list">
                    {suggestions.map((city, index) => (
                        <div
                            key={city}
                            className={`suggestion-item ${index === 0 ? 'active' : ''}`}
                            onClick={() => handleSelectCity(city)}
                        >
                            {city}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

const CustomDatePicker = ({ selectedDate, onChange }) => {
    const [isOpen, setIsOpen] = useState(false);
    const dateObj = selectedDate ? new Date(selectedDate) : new Date();
    const [currentMonth, setCurrentMonth] = useState(dateObj);

    const daysInMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0).getDate();
    const firstDay = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1).getDay();

    const handlePrevMonth = (e) => {
        e.stopPropagation();
        setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
    };

    const handleNextMonth = (e) => {
        e.stopPropagation();
        setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));
    };

    const handleDayClick = (day) => {
        const year = currentMonth.getFullYear();
        const month = (currentMonth.getMonth() + 1).toString().padStart(2, '0');
        const dayStr = day.toString().padStart(2, '0');
        onChange(`${year}-${month}-${dayStr}`);
        setIsOpen(false);
    };

    // Close on click outside could be handled by a backdrop or ref, but for simplicity relying on toggle
    return (
        <div className="custom-date-picker">
            <div className="date-trigger" onClick={() => setIsOpen(!isOpen)}>
                {selectedDate || "Select Date"}
                <span>📅</span>
            </div>
            {isOpen && (
                <div className="calendar-popup">
                    <div className="calendar-header">
                        <button onClick={handlePrevMonth}>&lt;</button>
                        <span>{currentMonth.toLocaleString('default', { month: 'long', year: 'numeric' })}</span>
                        <button onClick={handleNextMonth}>&gt;</button>
                    </div>
                    <div className="calendar-grid">
                        {['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'].map(d => (
                            <div key={d} className="calendar-day-label">{d}</div>
                        ))}
                        {Array(firstDay).fill(null).map((_, i) => (
                            <div key={`empty-${i}`} className="calendar-day empty"></div>
                        ))}
                        {Array(daysInMonth).fill(null).map((_, i) => {
                            const day = i + 1;
                            const isSelected = selectedDate === `${currentMonth.getFullYear()}-${(currentMonth.getMonth() + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;

                            return (
                                <div key={day}
                                    className={`calendar-day ${isSelected ? 'selected' : ''}`}
                                    onClick={() => handleDayClick(day)}>
                                    {day}
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
};

const Travel = () => {
    // --- State ---
    const [searchParams, setSearchParams] = useState({
        from: 'İzmir',
        to: 'Bursa',
        date: '2026-02-15'
    });

    const [expeditions, setExpeditions] = useState([]);
    const [hasSearched, setHasSearched] = useState(false);
    const [searchError, setSearchError] = useState(null);

    // Selection State
    const [selectedExpeditionId, setSelectedExpeditionId] = useState(null);
    const [selectedSeat, setSelectedSeat] = useState(null);

    // Ticket State
    const [purchasedTicket, setPurchasedTicket] = useState(null);

    // --- Mock Data Generators ---
    const generateSeats = () => {
        const tickets = [];
        for (let i = 1; i <= 40; i++) {
            // Randomly assign reserved status (approx 30% reserved)
            const isReserved = Math.random() < 0.3;
            tickets.push({
                id: i,
                number: i,
                status: isReserved ? 'reserved' : 'available'
            });
        }
        return tickets;
    };

    const fetchExpeditions = async () => {
        const body = {
            departureCity: searchParams.from,
            arrivalCity: searchParams.to,
            date: searchParams.date
        };
        console.log("Fetching expeditions with:", body);
        setSearchError(null);
        try {
            const response = await fetch('/api/expedition/customer/get/search/expeditions', {
                method: 'POST',
                credentials: "include",
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                setSearchError("No expedition found or error occurred.");
                return [];
            }

            const data = await response.json();
            const rawList = data.expeditions || [];

            if (rawList.length === 0) {
                setSearchError("No expeditions found.");
            }

            return rawList.map(item => ({
                id: item.expeditionId,
                companyName: item.companyName,
                departureTime: item.time ? item.time.substring(0, 5) : "00:00",
                arrivalTime: calculateArrival(item.time, item.duration),
                price: item.price,
                duration: formatDuration(item.duration),
                from: item.departureCity,
                to: item.arrivalCity,
                date: item.date,
                tickets: null
            }));

        } catch (error) {
            console.error("Error fetching expeditions:", error);
            setSearchError("No expeditions found (Connection Error).");
            return [];
        }
    };

    const fetchSeats = async (expeditionId) => {
        try {
            const response = await fetch('/api/expedition/customer/get/search/seats', {
                method: 'POST',
                credentials: "include",
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ expeditionId })
            });

            if (!response.ok) {
                console.error("Failed to fetch tickets");
                return null;
            }

            const data = await response.json();
            console.log("Fetched tickets:", data.message);
            return data.tickets;
        } catch (error) {
            console.error("Error fetching tickets:", error);
            return null;
        }
    };

    // Helper to format duration (assuming int minutes from API)
    const formatDuration = (minutes) => {
        const h = Math.floor(minutes / 60);
        const m = minutes % 60;
        return `${h}h ${m}m`;
    };

    // Helper to calc arrival
    const calculateArrival = (timeStr, durationMinutes) => {
        if (!timeStr) return "00:00";
        const [hours, mins] = timeStr.split(':').map(Number);
        const totalMins = hours * 60 + mins + durationMinutes;
        const newH = Math.floor(totalMins / 60) % 24;
        const newM = totalMins % 60;
        return `${newH.toString().padStart(2, '0')}:${newM.toString().padStart(2, '0')}`;
    };

    // --- Handlers ---
    const handleSearch = async () => {
        console.log("Searching for:", searchParams);
        // const results = mockFetchExpeditions(); // OLD MOCK
        const results = await fetchExpeditions();
        setExpeditions(results);
        setHasSearched(true);
        setSelectedExpeditionId(null);
        setSelectedSeat(null);
        setPurchasedTicket(null);
    };

    const handleSwap = () => {
        setSearchParams(prev => ({
            ...prev,
            from: prev.to,
            to: prev.from
        }));
    };

    const toggleExpedition = async (id) => {
        if (selectedExpeditionId === id) {
            setSelectedExpeditionId(null);
        } else {
            setSelectedExpeditionId(id);
            setSelectedSeat(null);
            const expIndex = expeditions.findIndex(e => e.id === id);
            if (expIndex !== -1 && !expeditions[expIndex].tickets) {
                const tickets = await fetchSeats(id);

                console.log("Fetched tickets:", tickets);
                setExpeditions(prev => {
                    const next = [...prev];
                    if (next[expIndex]) {
                        // Map raw tickets to internal structure
                        next[expIndex].tickets = tickets.map(s => ({
                            id: s.seatNo,
                            number: s.seatNo,
                            status: String(s.status).toLowerCase() === 'reserved' ? 'reserved' : 'available'
                        }));
                    }
                    return next;
                });
            }
        }
    };

    const handleSeatClick = (seat) => {
        if (seat.status === 'reserved') return;
        setSelectedSeat(seat);
    };

    const handlePurchase = (expedition) => {
        if (!selectedSeat) return;

        const ticket = {
            pnr: 'PNR' + Math.floor(Math.random() * 1000000),
            seatNo: selectedSeat.number,
            expeditionId: expedition.id,
            companyName: expedition.companyName,
            from: expedition.from,
            to: expedition.to,
            date: expedition.date,
            time: expedition.departureTime,
            price: expedition.price
        };
        setPurchasedTicket(ticket);
        console.log("Ticket Purchased!", ticket);
    };

    return (
        <div className="travel-page-container">
            {/* Search Section */}
            <div className="search-section">
                <div className="input-group">
                    <label>From</label>
                    <div className="input-wrapper" style={{ padding: 0 }}>
                        <CitySearchInput
                            value={searchParams.from}
                            onChange={(val) => setSearchParams({ ...searchParams, from: val })}
                            placeholder="Select City"
                        />
                    </div>
                </div>

                <div className="swap-icon" onClick={handleSwap}>
                    ↔
                </div>

                <div className="input-group">
                    <label>To</label>
                    <div className="input-wrapper" style={{ padding: 0 }}>
                        <CitySearchInput
                            value={searchParams.to}
                            onChange={(val) => setSearchParams({ ...searchParams, to: val })}
                            placeholder="Select City"
                        />
                    </div>
                </div>

                {/* Date Input Replaced with CustomDatePicker */}
                <div className="input-group" style={{ flex: 0.5 }}>
                    <label>Departure</label>
                    <div className="input-wrapper" style={{ padding: 0, backgroundColor: 'transparent' }}>
                        <CustomDatePicker
                            selectedDate={searchParams.date}
                            onChange={(newDate) => setSearchParams({ ...searchParams, date: newDate })}
                        />
                    </div>
                </div>

                <button className="search-btn" onClick={handleSearch}>
                    Search
                </button>
            </div>

            {/* Ticket Info Modal */}
            {purchasedTicket && (
                <div className="modal-overlay" onClick={() => setPurchasedTicket(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="modal-close-btn" onClick={() => setPurchasedTicket(null)}>
                            &times;
                        </button>
                        <div className="success-icon">✓</div>
                        <h3>Ticket Purchased Successfully!</h3>

                        <div className="ticket-details">
                            <p><strong>PNR:</strong> {purchasedTicket.pnr}</p>
                            <p><strong>Seat:</strong> {purchasedTicket.seatNo}</p>
                            <p><strong>Route:</strong> {purchasedTicket.from} - {purchasedTicket.to}</p>
                            <p><strong>Time:</strong> {purchasedTicket.time}</p>
                            <p><strong>Price:</strong> {purchasedTicket.price} TL</p>
                        </div>
                    </div>
                </div>
            )}

            {/* Expeditions List */}
            <div className="expeditions-container">
                {searchError && (
                    <div className="search-error-message" style={{ color: 'red', textAlign: 'center', padding: '20px' }}>
                        {searchError}
                    </div>
                )}
                {!searchError && hasSearched && expeditions.length === 0 && (
                    <p>No expeditions found.</p>
                )}

                {expeditions.map(exp => (
                    <div key={exp.id} className={`expedition-card ${selectedExpeditionId === exp.id ? 'expanded' : ''}`}>
                        {/* Card Header (Visible Always) */}
                        <div className="card-header" onClick={() => toggleExpedition(exp.id)}>
                            <div className="company-info">
                                <h3>{exp.companyName}</h3>
                            </div>
                            <div className="time-info">
                                <span className="time">{exp.departureTime}</span>
                                <span className="duration">({exp.duration})</span>
                                <span className="time">{exp.arrivalTime}</span>
                            </div>
                            <div className="price-info">
                                <span className="price">{exp.price} TL</span>
                                <button className="select-btn">
                                    {selectedExpeditionId === exp.id ? 'Close' : 'Select'}
                                </button>
                            </div>
                        </div>

                        {/* Expanded Content (Seats) */}
                        {selectedExpeditionId === exp.id && (
                            <div className="seat-selection-area">
                                <h4>Select a Seat</h4>
                                <div className="bus-layout">
                                    {!exp.tickets ? (
                                        <div className="loading-tickets">Loading tickets...</div>
                                    ) : (
                                        exp.tickets.map(seat => (
                                            <div
                                                key={seat.id}
                                                className={`seat ${seat.status} ${selectedSeat?.id === seat.id ? 'selected' : ''}`}
                                                onClick={() => handleSeatClick(seat)}
                                            >
                                                {seat.number}
                                            </div>
                                        ))
                                    )}
                                </div>
                                <div className="purchase-action">
                                    <p>Selected Seat: <strong>{selectedSeat ? selectedSeat.number : '-'}</strong></p>
                                    <button
                                        className="purchase-btn"
                                        disabled={!selectedSeat}
                                        onClick={() => handlePurchase(exp)}
                                    >
                                        Purchase Ticket
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                ))}
            </div>

        </div>
    );
};

export default Travel;
