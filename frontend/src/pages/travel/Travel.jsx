import React, { useState } from 'react';
import './Travel.css';

const Travel = () => {
    // --- State ---
    const [searchParams, setSearchParams] = useState({
        from: 'Istanbul Europe',
        to: 'Ankara',
        date: '2025-12-18',
        isTomorrow: true
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

        // Alert/Console for now, can be a modal too
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

                <div className="input-group" style={{ flex: 0.5 }}>
                    <label>Departure</label>
                    <div className="input-wrapper">
                        <input
                            type="date"
                            value={searchParams.date}
                            onChange={(e) => setSearchParams({ ...searchParams, date: e.target.value })}
                        />
                    </div>
                </div>

                <div className="date-options">
                    <label className="radio-option">
                        <input
                            type="radio"
                            name="datePreset"
                            checked={!searchParams.isTomorrow}
                            onChange={() => setSearchParams(prev => ({ ...prev, isTomorrow: false }))}
                        /> Today
                    </label>
                    <label className="radio-option">
                        <input
                            type="radio"
                            name="datePreset"
                            checked={searchParams.isTomorrow}
                            onChange={() => setSearchParams(prev => ({ ...prev, isTomorrow: true }))}
                        /> Tomorrow
                    </label>
                </div>

                <button className="search-btn" onClick={handleSearch}>
                    Search
                </button>
            </div>

            {/* Ticket Info Message */}
            {purchasedTicket && (
                <div className="success-message">
                    <h3>Ticket Purchased Successfully!</h3>
                    <p><strong>PNR:</strong> {purchasedTicket.pnr}</p>
                    <p><strong>Seat:</strong> {purchasedTicket.seatNo}</p>
                    <p><strong>Route:</strong> {purchasedTicket.from} - {purchasedTicket.to}</p>
                    <p><strong>Time:</strong> {purchasedTicket.time}</p>
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
