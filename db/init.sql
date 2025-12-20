CREATE DATABASE memberDB;
CREATE DATABASE expeditionDB;
CREATE DATABASE securityDB;
CREATE DATABASE paymentDB;
/*

-- Member Service Tables
CREATE TABLE IF NOT EXISTS Customer
(
    id             INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name           VARCHAR(255),
    surname        VARCHAR(255),
    gender         VARCHAR(255),
    email          VARCHAR(255) UNIQUE,
    passwordHashed TEXT,
    createdAt      TIMESTAMPTZ DEFAULT now(),
    updatedAt      TIMESTAMPTZ DEFAULT now()
);
CREATE TABLE IF NOT EXISTS Admin
(
    id               INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name             VARCHAR(255),
    surname          VARCHAR(255),
    email            VARCHAR(255) UNIQUE,
    passwordHashed   TEXT,
    referenceAdminId INTEGER REFERENCES Admin (id),
    createdAt        TIMESTAMPTZ DEFAULT now()
);
CREATE TABLE IF NOT EXISTS Company
(
    id               INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title            VARCHAR(255),
    email            VARCHAR(255) UNIQUE,
    isVerified       BOOLEAN     DEFAULT FALSE,
    passwordHashed   TEXT,
    referenceAdminId INTEGER REFERENCES Admin (id),
    createdAt        TIMESTAMPTZ DEFAULT now(),
    updatedAt        TIMESTAMPTZ DEFAULT now(),
    verifiedAt       TIMESTAMPTZ DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS FavoriteCompany
(
    id         INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    customerId INTEGER REFERENCES Customer (id),
    companyId  INTEGER REFERENCES Company (id)
);
-- Expedition Service Tables
CREATE TABLE IF NOT EXISTS City
(
    id   INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Expedition
(
    id              INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    departureCityId INTEGER REFERENCES City (id),
    arrivalCityId   INTEGER REFERENCES City (id),
    date            DATE,
    time            TIME,
    price           NUMERIC(10, 2),
    duration        INTEGER, -- In Minutes
    companyId       INTEGER REFERENCES Company (id)
);

CREATE TABLE IF NOT EXISTS Seat
(
    id           INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    expeditionId INTEGER REFERENCES Expedition (id),
    seatNo       INTEGER,
    customerId   INTEGER REFERENCES Customer (id),
    status       VARCHAR(255),
    UNIQUE (expeditionId, seatNo)
);
CREATE TABLE IF NOT EXISTS Ticket
(
    id         INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    seatId     INTEGER REFERENCES Seat (id),
    paymentId  INTEGER,
    createdAt  TIMESTAMPTZ DEFAULT now(),
    customerId INTEGER REFERENCES Customer (id)
);

CREATE TABLE IF NOT EXISTS CustomerSession
(
    id         INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    customerId INTEGER REFERENCES Customer (id),
    code       VARCHAR(255),
    createdAt  TIMESTAMPTZ DEFAULT now(),
    updatedAt  TIMESTAMPTZ DEFAULT now(),
    expiresAt  TIMESTAMPTZ
);
CREATE TABLE IF NOT EXISTS AdminSession
(
    id        INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    adminId   INTEGER REFERENCES Admin (id),
    code      VARCHAR(255),
    createdAt TIMESTAMPTZ DEFAULT now(),
    updatedAt TIMESTAMPTZ DEFAULT now(),
    expiresAt TIMESTAMPTZ
);
CREATE TABLE IF NOT EXISTS CompanySession
(
    id        INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    companyId INTEGER REFERENCES Company (id),
    code      VARCHAR(255),
    createdAt TIMESTAMPTZ DEFAULT now(),
    updatedAt TIMESTAMPTZ DEFAULT now(),
    expiresAt TIMESTAMPTZ DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS Card
(
    id             INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    cardNo         VARCHAR(255),
    expirationDate DATE,
    CVC            CHAR(3),
    name           VARCHAR(255),
    surname        VARCHAR(255),
    customerId     INTEGER
);

CREATE TABLE IF NOT EXISTS Payment
(
    id     INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    cardID INTEGER REFERENCES Card (id),
    amount NUMERIC(10, 2),
    date   DATE
);
*/