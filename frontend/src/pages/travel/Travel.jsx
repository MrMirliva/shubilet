import React, { useState } from 'react';
import './Travel.css';

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
        from: 'Istanbul Europe',
        to: 'Ankara',
        date: '2025-12-18'
    });

    const [expeditions, setExpeditions] = useState([]);
    const [hasSearched, setHasSearched] = useState(false);

    // Selection State
    const [selectedExpeditionId, setSelectedExpeditionId] = useState(null);
    const [selectedSeat, setSelectedSeat] = useState(null);

    // Ticket State
    const [purchasedTicket, setPurchasedTicket] = useState(null);

    // --- Mock Data Generators ---
    const generateSeats = () => {
        const seats = [];
        for (let i = 1; i <= 40; i++) {
            // Randomly assign reserved status (approx 30% reserved)
            const isReserved = Math.random() < 0.3;
            seats.push({
                id: i,
                number: i,
                status: isReserved ? 'reserved' : 'available'
            });
        }
        return seats;
    };

    const mockFetchExpeditions = () => {
        // Simulate API call result
        const mockResults = [
            {
                id: 1,
                companyName: 'Metro Turizm',
                departureTime: '10:00',
                arrivalTime: '16:00',
                price: 500,
                duration: '6h 00m',
                from: searchParams.from,
                to: searchParams.to,
                date: searchParams.date,
                seats: generateSeats()
            },
            {
                id: 2,
                companyName: 'Pamukkale',
                departureTime: '12:30',
                arrivalTime: '18:45',
                price: 550,
                duration: '6h 15m',
                from: searchParams.from,
                to: searchParams.to,
                date: searchParams.date,
                seats: generateSeats()
            },
            {
                id: 3,
                companyName: 'Kamil Koc',
                departureTime: '15:00',
                arrivalTime: '21:00',
                price: 520,
                duration: '6h 00m',
                from: searchParams.from,
                to: searchParams.to,
                date: searchParams.date,
                seats: generateSeats()
            },
            {
                id: 4,
                companyName: 'Varan',
                departureTime: '23:00',
                arrivalTime: '06:00',
                price: 600,
                duration: '7h 00m',
                from: searchParams.from,
                to: searchParams.to,
                date: searchParams.date,
                seats: generateSeats()
            }
        ];
        return mockResults;
    };

    // --- Handlers ---
    const handleSearch = () => {
        console.log("Searching for:", searchParams);
        const results = mockFetchExpeditions();
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

    const toggleExpedition = (id) => {
        if (selectedExpeditionId === id) {
            setSelectedExpeditionId(null);
        } else {
            setSelectedExpeditionId(id);
            setSelectedSeat(null); // Reset seat selection when changing bus
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
                    <div className="input-wrapper">
                        <input
                            type="text"
                            value={searchParams.from}
                            onChange={(e) => setSearchParams({ ...searchParams, from: e.target.value })}
                        />
                    </div>
                </div>

                <div className="swap-icon" onClick={handleSwap}>
                    ↔
                </div>

                <div className="input-group">
                    <label>To</label>
                    <div className="input-wrapper">
                        <input
                            type="text"
                            value={searchParams.to}
                            onChange={(e) => setSearchParams({ ...searchParams, to: e.target.value })}
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
                {hasSearched && expeditions.length === 0 && (
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
                                    {exp.seats.map(seat => (
                                        <div
                                            key={seat.id}
                                            className={`seat ${seat.status} ${selectedSeat?.id === seat.id ? 'selected' : ''}`}
                                            onClick={() => handleSeatClick(seat)}
                                        >
                                            {seat.number}
                                        </div>
                                    ))}
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
