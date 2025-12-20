
import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Travel.css";

// Simple top bar if needed, or we rely on the in-page header
function TravelHeader() {
    return (
        <div style={{ position: "absolute", top: 20, left: 20, zIndex: 10 }}>
            <Link to="/" style={{ textDecoration: "none", fontWeight: "bold", color: "#0056D2" }}>
                ← Home
            </Link>
        </div>
    );
}

const CITIES = [
    "Adana",
    "Adıyaman",
    "Afyonkarahisar",
    "Ağrı",
    // ... (keeping full list)
    "Aksaray",
    "Amasya",
    "Ankara",
    "Antalya",
    "Ardahan",
    "Artvin",
    "Aydın",
    "Balıkesir",
    "Bartın",
    "Batman",
    "Bayburt",
    "Bilecik",
    "Bingöl",
    "Bitlis",
    "Bolu",
    "Burdur",
    "Bursa",
    "Çanakkale",
    "Çankırı",
    "Çorum",
    "Denizli",
    "Diyarbakır",
    "Düzce",
    "Edirne",
    "Elazığ",
    "Erzincan",
    "Erzurum",
    "Eskişehir",
    "Gaziantep",
    "Giresun",
    "Gümüşhane",
    "Hakkari",
    "Hatay",
    "Iğdır",
    "Isparta",
    "İstanbul",
    "İzmir",
    "Kahramanmaras",
    "Karabük",
    "Karaman",
    "Kars",
    "Kastamonu",
    "Kayseri",
    "Kırıkkale",
    "Kırklareli",
    "Kırşehir",
    "Kilis",
    "Kocaeli",
    "Konya",
    "Kütahya",
    "Malatya",
    "Manisa",
    "Mardin",
    "Mersin",
    "Muğla",
    "Muş",
    "Nevşehir",
    "Niğde",
    "Ordu",
    "Osmaniye",
    "Rize",
    "Sakarya",
    "Samsun",
    "Siirt",
    "Sinop",
    "Sivas",
    "Şanlıurfa",
    "Şırnak",
    "Tekirdağ",
    "Tokat",
    "Trabzon",
    "Tunceli",
    "Uşak",
    "Van",
    "Yalova",
    "Yozgat",
    "Zonguldak"
];

// Mock Saved Cards
const MOCK_SAVED_CARDS = [
    { cardId: "card_1", last4Digits: "4242", expirationMonth: "12", expirationYear: "25" },
    { cardId: "card_2", last4Digits: "8888", expirationMonth: "08", expirationYear: "28" },
    { cardId: "card_3", last4Digits: "1111", expirationMonth: "01", expirationYear: "30" },
];

export default function Travel() {
    const navigate = useNavigate();

    // Search filter states
    const [fromCity, setFromCity] = useState("");
    const [toCity, setToCity] = useState("");


    // Default date: Tomorrow
    const [date, setDate] = useState(() => {
        const d = new Date();
        d.setDate(d.getDate() + 1);
        return d.toISOString().split("T")[0];
    });

    // Custom Date Picker State
    const [showCalendar, setShowCalendar] = useState(false);
    const [calendarViewDate, setCalendarViewDate] = useState(new Date()); // For navigation
    const calendarRef = useRef(null);

    // Data states
    const [expeditions, setExpeditions] = useState([]); // Start empty
    const [isLoading, setIsLoading] = useState(false);
    const [hasSearched, setHasSearched] = useState(false); // To distinguish initial load vs empty search result

    // Inline Seat Selection States
    const [expandedExpeditionId, setExpandedExpeditionId] = useState(null);
    const [seats, setSeats] = useState([]); // Array of {seatNo, status}
    const [selectedSeat, setSelectedSeat] = useState(null); // Single selection
    const [isSeatsLoading, setIsSeatsLoading] = useState(false);

    // Payment Modal State
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [isProcessingPayment, setIsProcessingPayment] = useState(false);

    // Close calendar when clicking outside
    useEffect(() => {
        function handleClickOutside(event) {
            if (calendarRef.current && !calendarRef.current.contains(event.target)) {
                setShowCalendar(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // Sync calendar view date with selected date on open
    useEffect(() => {
        if (showCalendar && date) {
            setCalendarViewDate(new Date(date));
        }
    }, [showCalendar, date]);

    const fetchExpeditions = async () => {
        setIsLoading(true);
        setExpeditions([]);
        setHasSearched(true);
        setExpandedExpeditionId(null); // Reset expansion

        try {
            const response = await fetch("/api/expedition/customer/get/search/expeditions", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    departureCity: fromCity,
                    arrivalCity: toCity,
                    date: date
                })
            });

            if (!response.ok) {
                // Handle error (could show a toast or alert)
                console.error("Search failed:", response.statusText);
                setIsLoading(false);
                return;
            }

            const data = await response.json();
            if (data.expeditions) {
                setExpeditions(data.expeditions);
            }
        } catch (error) {
            console.error("Error fetching expeditions:", error);
        } finally {
            setIsLoading(false);
        }
    };


    const handleSearch = (e) => {
        e.preventDefault();
        fetchExpeditions();
    };

    const toggleExpedition = async (expeditionId) => {
        if (expandedExpeditionId === expeditionId) {
            setExpandedExpeditionId(null);
            setSeats([]);
            setSelectedSeat(null);
            return;
        }

        setExpandedExpeditionId(expeditionId);
        setSelectedSeat(null);
        setIsSeatsLoading(true);
        setSeats([]);

        // Mock Seat API Call
        await new Promise(resolve => setTimeout(resolve, 600));

        // Generate mock seats (e.g. 40 seats)
        const mockSeats = [];
        for (let i = 1; i <= 40; i++) {
            // Randomly assign status
            const isReserved = Math.random() < 0.3; // 30% chance reserved
            mockSeats.push({
                expeditionId: expeditionId,
                seatNo: i,
                status: isReserved ? "RESERVED" : "AVAILABLE"
            });
        }

        setSeats(mockSeats);
        setIsSeatsLoading(false);
    };

    const onSelectSeat = (seatNo) => {
        if (selectedSeat === seatNo) {
            setSelectedSeat(null);
        } else {
            setSelectedSeat(seatNo);
        }
    };

    const handleProceedToCheckout = () => {
        if (!selectedSeat || !expandedExpeditionId) return;
        setShowPaymentModal(true);
    };

    const handlePayWithCard = async (cardId) => {
        setIsProcessingPayment(true);
        // Simulate payment delay
        await new Promise(resolve => setTimeout(resolve, 1500));
        setIsProcessingPayment(false);
        setShowPaymentModal(false);
        alert(`Payment Successful with card ${cardId}! Ticket for Seat ${selectedSeat} booked.`);
        setExpandedExpeditionId(null); // Close expansion
    };

    // --- Custom Calendar Helpers ---
    const getDaysInMonth = (year, month) => new Date(year, month + 1, 0).getDate();
    const getFirstDayOfMonth = (year, month) => {
        // 0 = Sun, 1 = Mon ... 
        // We want 1 = Mon, ... 7 = Sun for our grid
        let day = new Date(year, month, 1).getDay();
        return day === 0 ? 6 : day - 1; // Shift so Mon is 0 index
    };

    const renderCalendarDays = () => {
        const year = calendarViewDate.getFullYear();
        const month = calendarViewDate.getMonth();
        const daysInMonth = getDaysInMonth(year, month);
        const startDay = getFirstDayOfMonth(year, month);

        const days = [];
        // Empty slots for start
        for (let i = 0; i < startDay; i++) {
            days.push(<div key={`empty-${i}`} className="calendarDay empty"></div>);
        }

        for (let d = 1; d <= daysInMonth; d++) {
            const currentDayStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
            const isSelected = date === currentDayStr;
            const isToday = currentDayStr === new Date().toISOString().split("T")[0];

            days.push(
                <div
                    key={d}
                    className={`calendarDay ${isSelected ? "selected" : ""} ${isToday ? "today" : ""}`}
                    onClick={() => {
                        setDate(currentDayStr);
                        setShowCalendar(false);
                    }}
                >
                    {d}
                </div>
            );
        }
        return days;
    };

    const nextMonth = () => {
        setCalendarViewDate(new Date(calendarViewDate.getFullYear(), calendarViewDate.getMonth() + 1, 1));
    };

    const prevMonth = () => {
        setCalendarViewDate(new Date(calendarViewDate.getFullYear(), calendarViewDate.getMonth() - 1, 1));
    };

    const MONTH_NAMES = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];


    return (
        <>
            <TravelHeader />

            <div className="travelPage">
                <div className="travelCard">

                    {/* Header adapted from ProfilePage */}
                    <header className="travelHeader">
                        <div>
                            <h1 className="title">Find Your <span>Expedition</span></h1>
                            <p className="subtitle">Search for the best bus tickets across the country</p>
                        </div>
                    </header>

                    <div className="travelContent">
                        <form className="searchForm" onSubmit={handleSearch}>
                            <div className="formGroup">
                                <label className="formLabel">From</label>
                                <select
                                    className="formInput"
                                    value={fromCity}
                                    onChange={(e) => setFromCity(e.target.value)}
                                >
                                    <option value="">Select city</option>
                                    {CITIES.map(c => (
                                        <option key={c} value={c}>{c}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="formGroup">
                                <label className="formLabel">To</label>
                                <select
                                    className="formInput"
                                    value={toCity}
                                    onChange={(e) => setToCity(e.target.value)}
                                >
                                    <option value="">Select city</option>
                                    {CITIES.map(c => (
                                        <option key={c} value={c}>{c}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="formGroup dateGroup" ref={calendarRef}>
                                <label className="formLabel">Date</label>
                                {/* Custom Date Input Display */}
                                <div
                                    className="customDateInput"
                                    onClick={() => setShowCalendar(!showCalendar)}
                                >
                                    <span className="dateText">{date || "Select Date"}</span>
                                    <span className="calendarIcon">📅</span>
                                </div>

                                {/* Custom Calendar Popup */}
                                {showCalendar && (
                                    <div className="calendarPopup">
                                        <div className="calendarHeader">
                                            <button type="button" onClick={prevMonth}>&lt;</button>
                                            <span>{MONTH_NAMES[calendarViewDate.getMonth()]} {calendarViewDate.getFullYear()}</span>
                                            <button type="button" onClick={nextMonth}>&gt;</button>
                                        </div>
                                        <div className="calendarWeekdays">
                                            <span>Mo</span><span>Tu</span><span>We</span><span>Th</span><span>Fr</span><span>Sa</span><span>Su</span>
                                        </div>
                                        <div className="calendarGrid">
                                            {renderCalendarDays()}
                                        </div>
                                    </div>
                                )}
                            </div>

                            <button type="submit" className="searchBtn" disabled={isLoading}>
                                {isLoading ? "Searching..." : "Search"}
                            </button>
                        </form>

                        <div className="listWrap">
                            {isLoading ? (
                                <div className="emptyBox">Searching...</div>
                            ) : !hasSearched ? (
                                <div className="emptyBox">
                                    Enter your travel details above to see available expeditions.
                                </div>
                            ) : expeditions.length === 0 ? (
                                <div className="emptyBox">
                                    No expeditions found matching your criteria.
                                </div>
                            ) : (
                                expeditions.map((exp) => {
                                    const isExpanded = expandedExpeditionId === exp.expeditionId;

                                    return (
                                        <div key={exp.expeditionId} className={`expeditionCard ${isExpanded ? "expanded" : ""}`}>
                                            <div className="cardMain">
                                                <div className="cardLeft">
                                                    <div className="routeInfo">
                                                        <span className="companyName">{exp.companyName}</span>
                                                        <div className="routeText">
                                                            {exp.departureCity}
                                                            <span className="routeArrow">→</span>
                                                            {exp.arrivalCity}
                                                        </div>
                                                    </div>

                                                    <div className="detailsGrid">
                                                        <div className="detailItem">
                                                            <span className="detailLabel">Date</span>
                                                            <span className="detailValue">{exp.date}</span>
                                                        </div>
                                                        <div className="detailItem">
                                                            <span className="detailLabel">Time</span>
                                                            <span className="detailValue">{exp.time}</span>
                                                        </div>
                                                        <div className="detailItem">
                                                            <span className="detailLabel">Duration</span>
                                                            <span className="detailValue">{Math.floor(exp.duration / 60)}h {exp.duration % 60}m</span>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div className="cardRight">
                                                    <span className="priceTag">${exp.price}</span>
                                                    <button
                                                        className="buyBtn"
                                                        onClick={() => toggleExpedition(exp.expeditionId)}
                                                    >
                                                        {isExpanded ? "Close" : "Buy Ticket"}
                                                    </button>
                                                </div>
                                            </div>

                                            {isExpanded && (
                                                <div className="seatSelectionArea">
                                                    <div className="seatAreaHeader">
                                                        <h3>Select your Seat</h3>
                                                        <div className="seatLegend">
                                                            <span className="legendItem"><span className="dot available"></span> Available</span>
                                                            <span className="legendItem"><span className="dot booked"></span> Reserved</span>
                                                            <span className="legendItem"><span className="dot selected"></span> Selected</span>
                                                        </div>
                                                    </div>

                                                    {isSeatsLoading ? (
                                                        <div className="seatsLoading">Loading seats...</div>
                                                    ) : (
                                                        <div className="seatGridContainer">
                                                            <div className="driverArea">
                                                                <span className="driverSeat">Driver</span>
                                                            </div>
                                                            <div className="busLayout">
                                                                {seats.map((seat) => (
                                                                    <button
                                                                        key={seat.seatNo}
                                                                        type="button"
                                                                        className={`seat ${seat.status.toLowerCase()} ${selectedSeat === seat.seatNo ? "selected" : ""}`}
                                                                        disabled={seat.status === "RESERVED"}
                                                                        onClick={() => onSelectSeat(seat.seatNo)}
                                                                    >
                                                                        {seat.seatNo}
                                                                    </button>
                                                                ))}
                                                            </div>
                                                        </div>
                                                    )}

                                                    <div className="seatFooter">
                                                        <div className="selectionSummary">
                                                            {selectedSeat ? `Seat #${selectedSeat} selected` : "No seat selected"}
                                                        </div>
                                                        <button
                                                            className="confirmBtn"
                                                            disabled={!selectedSeat}
                                                            onClick={handleProceedToCheckout}
                                                        >
                                                            Proceed to Checkout
                                                        </button>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    </div>

                    <p className="footerText">
                        <Link to="/company" className="link">Are you a company?</Link>
                    </p>
                </div>
            </div>

            {/* PAYMENT MODAL */}
            {showPaymentModal && (
                <div className="modalOverlay">
                    <div className="modalCard">
                        <div className="modalHeader">
                            <h3>Select Payment Method</h3>
                            <button className="closeModalBtn" onClick={() => setShowPaymentModal(false)}>✕</button>
                        </div>
                        <div className="modalBody">
                            <p className="modalSubtitle">Choose a saved card to complete your purchase for <span style={{ fontWeight: 'bold', color: '#0056D2' }}>Seat {selectedSeat}</span></p>

                            <div className="savedCardsList">
                                {MOCK_SAVED_CARDS.map(card => (
                                    <div key={card.cardId} className="savedCardItem" onClick={() => handlePayWithCard(card.cardId)}>
                                        <div className="cardIcon">💳</div>
                                        <div className="cardDetails">
                                            <div className="cardNumber">**** **** **** {card.last4Digits}</div>
                                            <div className="cardExpiry">Expires {card.expirationMonth}/{card.expirationYear}</div>
                                        </div>
                                        <button className="payNowBtn" disabled={isProcessingPayment}>
                                            {isProcessingPayment ? "Processing..." : "Pay"}
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
