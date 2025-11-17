-- Initialize required databases for services
-- This script runs only on first initialization of the Postgres volume

CREATE DATABASE "mainDB";
CREATE DATABASE "paymentDB";