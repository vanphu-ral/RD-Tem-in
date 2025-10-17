# Fix Summary: ProductName Field Missing in GraphQL Schema

## Problem

After merging code, the application showed this error:

```
ApolloError: The variables input contains a field name 'productName' that is not defined for input object type 'CreateProductInput'
```

## Root Cause

The GraphQL schema definition for `CreateProductInput` was missing the `productName` field, even though:

- The Java DTO ([`CreateProductInput.java`](src/main/java/com/mycompany/myapp/graphql/dto/CreateProductInput.java:6)) already had the field
- The frontend service ([`generate-tem-in.service.ts`](src/main/webapp/app/entities/generate-tem-in/service/generate-tem-in.service.ts:412)) was sending the field
- The GraphQL type `ListProductOfRequest` had the field

## Solution

### 1. Added `productName` Field to GraphQL Schema

**File:** [`src/main/resources/graphql/schema.graphqls`](src/main/resources/graphql/schema.graphqls:17)

```graphql
input CreateProductInput {
    requestCreateTemId: Int
    sapCode: String!
    productName: String # ← ADDED THIS FIELD
    temQuantity: Int!
    partNumber: String!
    lot: String!
    initialQuantity: Int!
    vendor: String!
    userData1: String
    userData2: String
    userData3: String
    userData4: String
    userData5: String
    storageUnit: String
    expirationDate: String!
    manufacturingDate: String!
    arrivalDate: String!
}
```

### 2. Added Navigation After Save

**File:** [`src/main/webapp/app/entities/generate-tem-in/import/generate-tem-in-import.component.ts`](src/main/webapp/app/entities/generate-tem-in/import/generate-tem-in-import.component.ts:1)

**Changes:**

1. Imported `Router` from `@angular/router` (line 10)
2. Injected `Router` into constructor (line 113)
3. Added navigation logic after successful save (lines 462-467):

```typescript
// Navigate to detail page with the requestId
if (result?.requestId) {
    setTimeout(() => {
        this.router.navigate(["/generate-tem-in/detail", result.requestId]);
    }, 1000);
}
```

## How It Works Now

### Save Flow:

1. User imports Excel file and clicks "Lưu" (Save) button for a group
2. System creates request and products via GraphQL mutation
3. Backend returns:
    - `requestId`: ID of the created request in `list_request_create_tem` table
    - `products`: Array of created products with their IDs from `list_product_of_request` table
4. Frontend stores these IDs in the component:
    - `savedRequestIds.set(groupIndex, result.requestId)` - for future updates
    - Updates data source with product IDs and requestId
5. After 1 second delay, navigates to detail page: `/generate-tem-in/detail/{requestId}`

### Update Flow (when clicking save again):

1. System detects existing `requestId` for the group
2. Calls `updateRequestProducts` mutation instead of creating new request
3. Updates products while keeping the same `requestId`
4. Does NOT navigate (stays on import page for further edits)

## Benefits

- ✅ Fixed GraphQL schema mismatch error
- ✅ Product names are now properly saved and displayed
- ✅ Automatic navigation to detail page after successful save
- ✅ Uses correct `requestId` for navigation (from `list_request_create_tem` table)
- ✅ Product IDs are tracked for future updates
- ✅ Seamless user experience with automatic page transition

## Testing

1. Import an Excel file with product data
2. Click "Lưu" button for a group
3. Verify success message appears
4. Verify automatic navigation to detail page after 1 second
5. Verify detail page shows correct request and products
6. Verify URL contains correct requestId: `http://localhost:9000/generate-tem-in/detail/{id}`

## Related Files Modified

- [`src/main/resources/graphql/schema.graphqls`](src/main/resources/graphql/schema.graphqls:1) - Added productName field
- [`src/main/webapp/app/entities/generate-tem-in/import/generate-tem-in-import.component.ts`](src/main/webapp/app/entities/generate-tem-in/import/generate-tem-in-import.component.ts:1) - Added Router and navigation logic
