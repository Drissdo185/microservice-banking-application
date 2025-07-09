-- -- Postgres
-- DROP TABLE IF EXISTS cards;
-- DROP TABLE IF EXISTS card_transactions;
--
--
-- CREATE TABLE cards (
--                        id BIGSERIAL PRIMARY KEY,
--                        user_id BIGINT NOT NULL, -- Reference to user-service
--                        card_number VARCHAR(19) UNIQUE NOT NULL,
--                        card_holder_name VARCHAR(100) NOT NULL,
--                        expiry_month INTEGER NOT NULL CHECK (expiry_month >= 1 AND expiry_month <= 12),
--                        expiry_year INTEGER NOT NULL,
--                        card_type VARCHAR(20) NOT NULL DEFAULT 'CREDIT', -- CREDIT, DEBIT
--                        card_status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, BLOCKED, EXPIRED
--
--     -- Balances
--                        credit_limit DECIMAL(15,2) NOT NULL DEFAULT 5000.00,
--                        current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
--                        available_balance DECIMAL(15,2) NOT NULL DEFAULT 5000.00,
--
--                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
--                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
-- );
--
--
-- CREATE TABLE card_transactions (
--                                    id BIGSERIAL PRIMARY KEY,
--                                    card_id BIGINT NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
--                                    amount DECIMAL(15,2) NOT NULL,
--                                    merchant_name VARCHAR(200),
--                                    description VARCHAR(500),
--                                    transaction_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
--
--                                    CONSTRAINT chk_amount_positive CHECK (amount > 0)
-- )

-- Testing
DROP TABLE IF EXISTS card_transactions;
DROP TABLE IF EXISTS cards;

CREATE TABLE cards (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       card_number VARCHAR(19) UNIQUE NOT NULL,
                       card_holder_name VARCHAR(100) NOT NULL,
                       expiry_month INTEGER NOT NULL CHECK (expiry_month >= 1 AND expiry_month <= 12),
                       expiry_year INTEGER NOT NULL,
                       card_type VARCHAR(20) NOT NULL DEFAULT 'CREDIT',
                       card_status VARCHAR(20) DEFAULT 'ACTIVE',

                       credit_limit DECIMAL(15,2) NOT NULL DEFAULT 5000.00,
                       current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                       available_balance DECIMAL(15,2) NOT NULL DEFAULT 5000.00,

                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE card_transactions (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   card_id BIGINT NOT NULL,
                                   amount DECIMAL(15,2) NOT NULL,
                                   merchant_name VARCHAR(200),
                                   description VARCHAR(500),
                                   transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_card FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
                                   CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

