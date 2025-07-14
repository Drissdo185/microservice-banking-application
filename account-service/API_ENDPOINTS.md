# Account Service API Endpoints

## Authentication
All endpoints require JWT token in Authorization header: `Authorization: Bearer <token>`

## Account Management

### Create Account
**POST** `/api/accounts`
```json
{
  "accountType": "CHECKING",
  "description": "Primary checking account"
}
```

**Response (201):**
```json
{
  "id": 1,
  "userId": 123,
  "accountNumber": "123456789012",
  "accountType": "CHECKING",
  "balance": 0.00,
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Get User Accounts
**GET** `/api/accounts`

**Response (200):**
```json
[
  {
    "id": 1,
    "userId": 123,
    "accountNumber": "123456789012",
    "accountType": "CHECKING",
    "balance": 1500.00,
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

### Get Account by ID
**GET** `/api/accounts/{accountId}`

**Response (200):** Same as account object above

### Get Account by Number
**GET** `/api/accounts/number/{accountNumber}`

**Response (200):** Same as account object above

### Close Account
**PUT** `/api/accounts/{accountId}/close`

**Response (200):** Account object with status "CLOSED"

## Transaction Management

### Add Transaction
**POST** `/api/accounts/{accountId}/transactions`
```json
{
  "amount": 500.00,
  "transactionType": "CREDIT",
  "description": "Salary deposit"
}
```

**Response (201):**
```json
{
  "id": 1,
  "accountId": 1,
  "amount": 500.00,
  "transactionType": "CREDIT",
  "description": "Salary deposit",
  "transactionDate": "2024-01-15T14:30:00"
}
```

### Get Account Transactions (Paginated)
**GET** `/api/accounts/{accountId}/transactions?page=0&size=20`

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "accountId": 1,
      "amount": 500.00,
      "transactionType": "CREDIT",
      "description": "Salary deposit",
      "transactionDate": "2024-01-15T14:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### Get Account Transaction History
**GET** `/api/accounts/{accountId}/transactions/history`

**Response (200):** Array of transaction objects

### Get All User Transactions (Paginated)
**GET** `/api/accounts/transactions?page=0&size=20`

**Response (200):** Same paginated structure as account transactions

## Business Rules

### Account Creation Limits
- **Checking accounts**: Maximum 3 per user
- **Savings accounts**: Maximum 1 per user

### Transaction Rules
- **Credit transactions**: Always allowed
- **Debit transactions**: Require sufficient balance
- **Account closure**: Only allowed with zero balance

### Error Responses

**400 Bad Request:**
```json
{
  "timestamp": "2024-01-15T14:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": {
    "accountType": "Account type is required"
  }
}
```

**404 Not Found:**
```json
{
  "timestamp": "2024-01-15T14:30:00",
  "status": 404,
  "error": "Account Not Found",
  "message": "Account not found with ID: 123"
}
```

**409 Conflict:**
```json
{
  "timestamp": "2024-01-15T14:30:00",
  "status": 409,
  "error": "Account Already Exists",
  "message": "User can only have one savings account"
}
```