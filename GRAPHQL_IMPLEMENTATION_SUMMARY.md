# GraphQL Implementation for Generate-Tem-In Import Feature

## Overview

This document summarizes the implementation of GraphQL mutations to save imported Excel data to the database for the generate-tem-in component.

## Changes Made

### 1. GraphQL Schema Updates

**File:** `src/main/resources/graphql/schema.graphqls`

Added new mutations and input types:

- `createProduct(input: CreateProductInput!)`: Creates a single product
- `createProductsBatch(requestId: Int!, products: [CreateProductInput!]!)`: Creates multiple products in batch
- `CreateProductInput`: Input type with all required product fields

### 2. Backend Java Implementation

#### a. DTO Class

**File:** `src/main/java/com/mycompany/myapp/graphql/dto/CreateProductInput.java` (NEW)

Created input DTO class with fields:

- requestCreateTemId
- sapCode
- temQuantity
- partNumber
- lot
- initialQuantity
- vendor
- userData1-5
- storageUnit
- expirationDate
- manufacturingDate
- arrivalDate

#### b. Service Layer

**File:** `src/main/java/com/mycompany/myapp/service/ListProductOfRequestService.java`

Added methods:

- `createProduct(CreateProductInput input)`: Creates single product
- `createProductsBatch(Integer requestId, List<CreateProductInput> inputs)`: Creates multiple products
- `mapInputToEntity(CreateProductInput input, ListProductOfRequest product)`: Maps DTO to entity
- `parseDate(String dateStr, DateTimeFormatter formatter)`: Parses date strings

#### c. GraphQL Resolver

**File:** `src/main/java/com/mycompany/myapp/graphql/ProductResolver.java`

Added mutation mappings:

- `@MutationMapping createProduct(@Argument CreateProductInput input)`
- `@MutationMapping createProductsBatch(@Argument Integer requestId, @Argument List<CreateProductInput> products)`

### 3. Frontend Angular Implementation

#### Service Updates

**File:** `src/main/webapp/app/entities/generate-tem-in/service/generate-tem-in.service.ts`

Changes:

- Updated `CREATE_PRODUCT_MUTATION` with correct field names (camelCase)
- Added `CREATE_PRODUCTS_BATCH_MUTATION` for batch operations
- Modified `importProductsFromExcel()` to use GraphQL mutation instead of REST API

The method now:

1. Converts `ExcelImportData[]` to `CreateProductInput[]` format
2. Calls GraphQL `createProductsBatch` mutation
3. Returns Observable of created products

## Data Flow

```
1. User imports Excel file
   ↓
2. Excel parsed to ExcelImportData[]
   ↓
3. User clicks "Save" button
   ↓
4. Frontend converts to CreateProductInput[]
   ↓
5. GraphQL mutation sent to backend
   ↓
6. Backend resolver receives request
   ↓
7. Service layer processes batch
   ↓
8. Repository saves to database
   ↓
9. Response returned to frontend
   ↓
10. Success message displayed
```

## Database Table

**Table:** `list_product_of_request`

Fields saved:

- Request_create_tem_id (FK)
- SAPCode
- Tem_quantity
- PartNumber
- LOT
- InitialQuantity
- Vendor
- UserData1-5
- StorageUnit
- ExpirationDate
- ManufacturingDate
- Arrival_date
- Number_of_prints (default: 0)

## Testing Instructions

### 1. Start the Application

```bash
# Backend
./mvnw spring-boot:run

# Frontend (in another terminal)
npm start
```

### 2. Test the Import Feature

1. Navigate to the generate-tem-in import page
2. Select an Excel file with product data
3. Click "Import" to load data into the table
4. Review the data in the table
5. Click "Save" button
6. Verify success message appears
7. Check database to confirm data was saved

### 3. Verify in Database

```sql
SELECT * FROM list_product_of_request
WHERE Request_create_tem_id = [your_request_id]
ORDER BY id DESC;
```

### 4. Test GraphQL Directly (Optional)

Use GraphQL Playground at `http://localhost:8080/graphql`

Example mutation:

```graphql
mutation {
    createProductsBatch(
        requestId: 1
        products: [
            {
                requestCreateTemId: 1
                sapCode: "12345678"
                temQuantity: 10
                partNumber: "PN-001"
                lot: "LOT-001"
                initialQuantity: 1000
                vendor: "Vendor A"
                userData1: "Data1"
                userData2: "Data2"
                userData3: "Data3"
                userData4: "Data4"
                userData5: "Data5"
                storageUnit: "Unit-A"
                expirationDate: "2025-12-31"
                manufacturingDate: "2025-01-01"
                arrivalDate: "2025-01-15"
            }
        ]
    ) {
        id
        sapCode
        partNumber
        lot
    }
}
```

## Benefits of GraphQL Implementation

1. **Type Safety**: GraphQL schema provides strong typing
2. **Batch Operations**: Efficient bulk insert with single request
3. **Flexible Queries**: Can request only needed fields
4. **Better Error Handling**: GraphQL provides detailed error messages
5. **API Documentation**: Schema serves as documentation
6. **No REST Endpoint Needed**: Eliminates need for custom REST controllers

## Troubleshooting

### Common Issues

1. **Date Format Errors**
    - Ensure dates are in ISO format: "YYYY-MM-DD"
    - Backend converts to LocalDateTime automatically

2. **Missing Required Fields**
    - Check that all required fields in CreateProductInput are provided
    - Use empty strings for optional userData fields if not provided

3. **GraphQL Errors**
    - Check browser console for detailed error messages
    - Verify GraphQL schema matches input structure
    - Ensure backend service is running

4. **Database Constraints**
    - Verify requestCreateTemId exists in parent table
    - Check field length constraints (e.g., SAPCode max 8 chars)

## Future Enhancements

1. Add validation for duplicate products
2. Implement update mutation for existing products
3. Add delete mutation for batch deletion
4. Implement pagination for large datasets
5. Add GraphQL subscriptions for real-time updates
6. Add error handling for partial batch failures

## Files Modified/Created

### Created:

- `src/main/java/com/mycompany/myapp/graphql/dto/CreateProductInput.java`
- `GRAPHQL_IMPLEMENTATION_SUMMARY.md`

### Modified:

- `src/main/resources/graphql/schema.graphqls`
- `src/main/java/com/mycompany/myapp/service/ListProductOfRequestService.java`
- `src/main/java/com/mycompany/myapp/graphql/ProductResolver.java`
- `src/main/webapp/app/entities/generate-tem-in/service/generate-tem-in.service.ts`

## Conclusion

The GraphQL implementation is now complete and ready for testing. The import feature can successfully save Excel data to the database using GraphQL mutations, providing a modern, type-safe, and efficient solution.
