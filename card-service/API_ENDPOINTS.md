# Card Service API Endpoints

Base URL: `http://localhost:8082`

## Card Management Endpoints

### 1. Create New Card
- **Method**: POST
- **URL**: `/api/cards`
- **Content-Type**: `application/json`

### 2. Get Card by ID
- **Method**: GET
- **URL**: `/api/cards/{id}`
- **Example**: `/api/cards/1`

### 3. Get Card by Card Number
- **Method**: GET
- **URL**: `/api/cards/card-number/{cardNumber}`
- **Example**: `/api/cards/card-number/1234567890123456`

### 4. Get Cards by User ID
- **Method**: GET
- **URL**: `/api/cards/user/{userId}`
- **Example**: `/api/cards/user/1001`

### 5. Get All Cards (Paginated)
- **Method**: GET
- **URL**: `/api/cards`
- **Query Parameters**: `page`, `size`, `sort`
- **Example**: `/api/cards?page=0&size=10&sort=id,desc`

### 6. Update Card
- **Method**: PUT
- **URL**: `/api/cards/{id}`
- **Content-Type**: `application/json`
- **Example**: `/api/cards/1`

### 7. Delete Card
- **Method**: DELETE
- **URL**: `/api/cards/{id}`
- **Example**: `/api/cards/1`

## Card Status Management

### 8. Update Card Status
- **Method**: PATCH
- **URL**: `/api/cards/{id}/status`
- **Query Parameters**: `status` (ACTIVE, BLOCKED, EXPIRED)
- **Example**: `/api/cards/1/status?status=BLOCKED`

### 9. Block Card
- **Method**: PATCH
- **URL**: `/api/cards/{id}/block`
- **Example**: `/api/cards/1/block`

### 10. Unblock Card
- **Method**: PATCH
- **URL**: `/api/cards/{id}/unblock`
- **Example**: `/api/cards/1/unblock`

### 11. Check if Card Expired
- **Method**: GET
- **URL**: `/api/cards/{id}/expired`
- **Example**: `/api/cards/1/expired`

## Balance Management

### 12. Update Balance
- **Method**: PATCH
- **URL**: `/api/cards/{id}/balance`
- **Query Parameters**: `amount`, `operation` (DEBIT, CREDIT)
- **Example**: `/api/cards/1/balance?amount=100.00&operation=DEBIT`

## Credit Limit Management

### 13. Increase Credit Limit
- **Method**: PATCH
- **URL**: `/api/cards/{id}/increase-limit`
- **Query Parameters**: `amount`
- **Example**: `/api/cards/1/increase-limit?amount=1000.00`

### 14. Decrease Credit Limit
- **Method**: PATCH
- **URL**: `/api/cards/{id}/decrease-limit`
- **Query Parameters**: `amount`
- **Example**: `/api/cards/1/decrease-limit?amount=500.00`