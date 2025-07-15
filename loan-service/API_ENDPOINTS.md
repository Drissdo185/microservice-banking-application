# Loan Service API Endpoints

Base URL: `http://localhost:8100`

## Loan Management Endpoints

### 1. Create New Loan
- **Method**: POST
- **URL**: `/api/loans`
- **Content-Type**: `application/json`
- **Authentication**: Required (Bearer Token)
- **Request Body**:
```json
{
  "userId": 1001,
  "loanAmount": 50000.00,
  "interestRate": 8.5,
  "tenureMonths": 36
}
```

### 2. Get Loan by ID
- **Method**: GET
- **URL**: `/api/loans/{id}`
- **Authentication**: Required (Bearer Token)
- **Example**: `/api/loans/1`

### 3. Get Loans by User ID (Current User)
- **Method**: GET
- **URL**: `/api/loans/user`
- **Authentication**: Required (Bearer Token)

### 4. Get Loans by User ID (Admin)
- **Method**: GET
- **URL**: `/api/loans/user/{userId}`
- **Authentication**: Required (Bearer Token)
- **Example**: `/api/loans/user/1001`

### 5. Get Active Loans by User ID
- **Method**: GET
- **URL**: `/api/loans/user/active`
- **Authentication**: Required (Bearer Token)

### 6. Get All Loans (Paginated)
- **Method**: GET
- **URL**: `/api/loans`
- **Authentication**: Required (Bearer Token)
- **Query Parameters**: `page`, `size`, `sort`
- **Example**: `/api/loans?page=0&size=10&sort=id,desc`

### 7. Update Loan
- **Method**: PUT
- **URL**: `/api/loans/{id}`
- **Content-Type**: `application/json`
- **Authentication**: Required (Bearer Token)
- **Example**: `/api/loans/1`
- **Request Body**:
```json
{
  "interestRate": 7.5,
  "tenureMonths": 24
}
```

### 8. Delete Loan
- **Method**: DELETE
- **URL**: `/api/loans/{id}`
- **Authentication**: Required (Bearer Token)
- **Example**: `/api/loans/1`

## Loan Status Management

### 9. Update Loan Status
- **Method**: PATCH
- **URL**: `/api/loans/{id}/status`
- **Authentication**: Required (Bearer Token)
- **Query Parameters**: `status` (ACTIVE, PAID, DEFAULT)
- **Example**: `/api/loans/1/status?status=PAID`

## Payment Management

### 10. Make Payment
- **Method**: POST
- **URL**: `/api/loans/{id}/payments`
- **Authentication**: Required (Bearer Token)
- **Query Parameters**: `amount`
- **Example**: `/api/loans/1/payments?amount=5000.00`

### 11. Get Payments by Loan ID
- **Method**: GET
- **URL**: `/api/loans/{id}/payments`
- **Authentication**: Required (Bearer Token)
- **Example**: `/api/loans/1/payments`

## Utility Endpoints

### 12. Calculate EMI
- **Method**: GET
- **URL**: `/api/loans/calculate-emi`
- **Authentication**: Not Required
- **Query Parameters**: `loanAmount`, `interestRate`, `tenureMonths`
- **Example**: `/api/loans/calculate-emi?loanAmount=50000&interestRate=8.5&tenureMonths=36`

### 13. Health Check
- **Method**: GET
- **URL**: `/api/loans/health`
- **Authentication**: Not Required
- **Example**: `/api/loans/health`

## Response Format

### Success Response
```json
{
  "id": 1,
  "userId": 1001,
  "loanAmount": 50000.00,
  "interestRate": 8.5,
  "tenureMonths": 36,
  "monthlyEmi": 1588.57,
  "outstandingAmount": 45000.00,
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Error Response
```json
{
  "status": 404,
  "error": "Loan Not Found",
  "message": "Loan not found with ID: 1",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Authentication

All endpoints except `/health` and `/calculate-emi` require JWT authentication:
- Include `Authorization: Bearer <token>` header
- Token should be obtained from User Service authentication endpoints

## Business Rules

1. **Loan Creation**:
   - Maximum 3 active loans per user
   - EMI is automatically calculated based on loan amount, interest rate, and tenure
   - Initial outstanding amount equals loan amount

2. **Payments**:
   - Payment amount cannot exceed outstanding balance
   - Loan status automatically changes to "PAID" when outstanding balance reaches zero
   - Only active loans can receive payments

3. **Loan Updates**:
   - Interest rate and tenure can be modified
   - EMI is recalculated automatically after updates
   - Cannot delete loans with outstanding balance