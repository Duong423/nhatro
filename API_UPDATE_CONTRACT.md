# API Update Contract

## Endpoint
```
PUT /api/contracts/{contractId}/update
```

## Authorization
Owner only

## Request Body
```json
{
  "startDate": "2024-02-01",
  "endDate": "2025-02-01",
  "monthlyRent": 3000000,
  "depositAmount": 3000000,
  "electricityCostPerUnit": 3500,
  "waterCostPerUnit": 20000,
  "serviceFee": 100000,
  "paymentCycle": "monthly",
  "numberOfTenants": 2,
  "terms": "Updated contract terms and conditions",
  "notes": "Updated notes"
}
```

## Response
```json
{
  "code": 200,
  "message": "Contract updated successfully",
  "result": {
    "contractId": 1,
    "bookingId": 1,
    "tenantId": 2,
    "tenantName": "Nguyen Van A",
    "tenantPhone": "0987654321",
    "tenantEmail": "tenant@example.com",
    "landlordId": 1,
    "landlordName": "Tran Thi B",
    "landlordPhone": "0123456789",
    "hostelId": 1,
    "hostelName": "Phong Tro ABC",
    "hostelAddress": "123 Nguyen Trai, Q1, TPHCM",
    "hostelPrice": 3000000,
    "hostelArea": 25,
    "hostelAmenities": "Wifi, dieu hoa, tu lanh",
    "startDate": "2024-02-01",
    "endDate": "2025-02-01",
    "monthlyRent": 3000000,
    "depositAmount": 3000000,
    "electricityCostPerUnit": 3500,
    "waterCostPerUnit": 20000,
    "serviceFee": 100000,
    "paymentCycle": "monthly",
    "numberOfTenants": 2,
    "terms": "Updated contract terms and conditions",
    "signedDate": "2024-01-15",
    "status": "ACTIVE",
    "notes": "Updated notes",
    "createdAt": "2024-01-10T10:00:00"
  }
}
```
