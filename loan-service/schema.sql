DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS loan_payments;

-- Loans
CREATE TABLE loans (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       loan_amount DECIMAL(10,2) NOT NULL,
                       interest_rate DECIMAL(5,2) NOT NULL,
                       tenure_months INTEGER NOT NULL,
                       monthly_emi DECIMAL(10,2) NOT NULL,
                       outstanding_amount DECIMAL(10,2) NOT NULL,
                       status VARCHAR(10) DEFAULT 'ACTIVE', -- ACTIVE, PAID, DEFAULT
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Loan payments
CREATE TABLE loan_payments (
                               id BIGSERIAL PRIMARY KEY,
                               loan_id BIGINT NOT NULL REFERENCES loans(id),
                               payment_amount DECIMAL(10,2) NOT NULL,
                               payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);