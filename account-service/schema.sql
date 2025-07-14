DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS account_transactions;
-- Bank accounts
CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          account_number VARCHAR(20) UNIQUE NOT NULL,
                          account_type VARCHAR(10) NOT NULL, -- CHECKING, SAVINGS
                          balance DECIMAL(10,2) DEFAULT 0.00,
                          status VARCHAR(10) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Account transactions
CREATE TABLE account_transactions (
                                      id BIGSERIAL PRIMARY KEY,
                                      account_id BIGINT NOT NULL REFERENCES accounts(id),
                                      amount DECIMAL(10,2) NOT NULL,
                                      transaction_type VARCHAR(10) NOT NULL, -- DEBIT, CREDIT
                                      description VARCHAR(255),
                                      transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);