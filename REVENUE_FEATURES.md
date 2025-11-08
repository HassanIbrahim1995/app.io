
# Hotel Management System - Revenue & Pricing Features

## Overview

This document covers the comprehensive product catalog, upselling engine, city tax configuration, and extra fees system designed to maximize hotel revenue while providing transparency to guests.

## 1. Product Catalog System

Complete product inventory management for all hotel services and amenities.

### Product Categories

- **MINIBAR** - In-room minibar items
- **ROOM_SERVICE** - Food and beverage delivery
- **SPA** - Spa treatments and wellness services
- **LAUNDRY** - Laundry and dry cleaning
- **GIFT_SHOP** - Souvenirs and retail items
- **RESTAURANT** - Restaurant services
- **PARKING** - Parking services
- **BUSINESS_CENTER** - Business services
- **GYM** - Fitness center access
- **POOL** - Pool and recreation
- **TRANSPORTATION** - Airport transfers, taxi
- **CONFERENCE** - Meeting room services
- **OTHER** - Miscellaneous services

### Product Features

#### Inventory Management
- Real-time stock tracking
- Low stock alerts
- Automatic stock status updates
- Min/max quantity settings
- Unit types (ITEM, HOUR, DAY, KG)

#### Pricing
- Base price and cost price
- Tax configuration per product
- Automatic tax calculation
- Bulk pricing support

#### Upselling Integration
- Mark products as upsell items
- Upsell priority ranking
- Custom upsell descriptions
- Display in recommendation engine

### API Endpoints

```
POST   /api/v1/products                    - Create product
PUT    /api/v1/products/{id}               - Update product
GET    /api/v1/products                    - List products (with filters)
GET    /api/v1/products/{id}               - Get product details
GET    /api/v1/products/sku/{sku}          - Get by SKU
GET    /api/v1/products/category/{category} - Get by category
GET    /api/v1/products/upsell             - Get upsell products
GET    /api/v1/products/low-stock          - Get low stock alert
PATCH  /api/v1/products/{id}/stock         - Update stock level
DELETE /api/v1/products/{id}               - Delete product
```

### Example Product

```json
{
  "sku": "SPA-MASSAGE-60",
  "name": "Swedish Massage 60 Minutes",
  "description": "Relaxing full body massage",
  "category": "SPA",
  "price": 120.00,
  "costPrice": 45.00,
  "quantity": 0,
  "minQuantity": 0,
  "status": "ACTIVE",
  "isTaxable": true,
  "taxRate": 10.00,
  "isUpsell": true,
  "upsellPriority": 10,
  "upsellDescription": "Enhance your stay with a rejuvenating massage",
  "unitType": "HOUR"
}
```

## 2. Upselling & Recommendation Engine

Intelligent upselling system to increase revenue through personalized recommendations.

### Offer Types

1. **ROOM_UPGRADE** - Upgrade to better room type
2. **PRODUCT** - Additional products/services
3. **SERVICE** - Add-on services
4. **PACKAGE** - Bundle deals

### Upsell Features

#### Smart Recommendations
- Room upgrade suggestions based on current booking
- Product recommendations by category
- Service bundles
- Time-sensitive offers

#### Offer Management
- Validity periods (from/to dates)
- Usage limits
- Discount percentages
- Display priority
- Terms and conditions

#### Conversion Tracking
- Track offer acceptance rate
- Monitor usage count
- Analyze revenue impact

### API Endpoints

```
GET    /api/v1/upsell/offers                        - Get active offers
GET    /api/v1/upsell/room-upgrades/{roomTypeId}    - Get upgrade options
GET    /api/v1/upsell/recommendations/{reservationId} - Personalized recommendations
POST   /api/v1/upsell/offers/{offerId}/accept       - Accept offer
```

### Example Upsell Offer

```json
{
  "title": "Upgrade to Deluxe Suite",
  "description": "Enjoy 50% more space with ocean view",
  "offerType": "ROOM_UPGRADE",
  "sourceRoomType": "Standard Double",
  "targetRoomType": "Deluxe Suite",
  "originalPrice": 150.00,
  "offerPrice": 100.00,
  "discountPercentage": 33.33,
  "validFrom": "2024-01-01",
  "validTo": "2024-03-31",
  "maxUses": 100,
  "usedCount": 23,
  "isActive": true,
  "displayPriority": 10
}
```

### Recommendation Logic

**For Reservations:**
1. Room upgrades from current room type
2. Top 5 product recommendations
3. Top 3 service recommendations
4. Seasonal/promotional packages

**Display Rules:**
- Show at booking time
- Email after confirmation
- Display in guest portal
- Suggest during check-in

## 3. City Tax Configuration

Flexible city/local tax system supporting multiple jurisdictions and calculation methods.

### Tax Calculation Types

1. **PERCENTAGE** - % of room rate
   ```
   Tax = Room Rate × Tax Rate
   Example: $200 × 12% = $24
   ```

2. **FIXED** - Fixed amount
   ```
   Tax = Fixed Amount
   Example: $15 per stay
   ```

3. **PER_NIGHT** - Fixed per night
   ```
   Tax = Fixed Amount × Nights
   Example: $5 × 3 nights = $15
   ```

4. **PER_PERSON** - Per person per night
   ```
   Tax = Amount × Persons × Nights
   Example: $3 × 2 persons × 3 nights = $18
   ```

### Tax Features

- Location-based (City, State, Country)
- Multiple taxes per location
- Effective date ranges
- Apply to room, services, or all charges
- Active/inactive toggle

### API Endpoints

```
POST   /api/v1/city-taxes          - Create city tax
GET    /api/v1/city-taxes           - List all taxes
GET    /api/v1/city-taxes/{city}    - Get taxes by city
PUT    /api/v1/city-taxes/{id}      - Update tax
DELETE /api/v1/city-taxes/{id}      - Delete tax
```

### Example City Taxes

```json
[
  {
    "city": "Miami",
    "state": "Florida",
    "country": "USA",
    "taxName": "Miami Tourist Development Tax",
    "taxRate": 3.00,
    "calculationType": "PERCENTAGE",
    "appliesTo": "ROOM",
    "isActive": true
  },
  {
    "city": "Miami",
    "taxName": "Convention Development Tax",
    "fixedAmount": 2.00,
    "calculationType": "PER_NIGHT",
    "appliesTo": "ROOM",
    "isActive": true
  }
]
```

## 4. Extra Fees System

Comprehensive fee management for additional charges.

### Fee Types

- **RESORT_FEE** - All-inclusive resort amenities
- **SERVICE_CHARGE** - Service charge percentage
- **CLEANING_FEE** - Additional cleaning fee
- **EARLY_CHECKIN** - Before standard time
- **LATE_CHECKOUT** - After standard time
- **PET_FEE** - Pet accommodation
- **PARKING** - Parking facility
- **WIFI** - Premium internet
- **MINIBAR_RESTOCKING** - Minibar service
- **DAMAGE_DEPOSIT** - Refundable deposit
- **CANCELLATION_FEE** - Cancellation charge
- **NO_SHOW_FEE** - No-show penalty
- **OTHER** - Other charges

### Calculation Basis

1. **PER_STAY** - One-time fee
2. **PER_NIGHT** - Per night of stay
3. **PER_PERSON** - Per guest
4. **PERCENTAGE** - % of room rate

### Fee Features

- Mandatory vs. optional
- Refundable vs. non-refundable
- Room type applicability
- Minimum stay requirements
- Display order control

### API Endpoints

```
POST   /api/v1/extra-fees              - Create fee
GET    /api/v1/extra-fees               - List all fees
GET    /api/v1/extra-fees/{id}          - Get fee details
GET    /api/v1/extra-fees/mandatory     - Get mandatory fees
PUT    /api/v1/extra-fees/{id}          - Update fee
DELETE /api/v1/extra-fees/{id}          - Delete fee
```

### Example Extra Fees

```json
[
  {
    "feeCode": "RESORT-FEE",
    "feeName": "Resort Fee",
    "description": "Includes pool, gym, and WiFi access",
    "feeType": "RESORT_FEE",
    "amount": 35.00,
    "calculationBasis": "PER_NIGHT",
    "isMandatory": true,
    "isRefundable": false
  },
  {
    "feeCode": "EARLY-CHECKIN",
    "feeName": "Early Check-in Fee",
    "description": "Check-in before 2 PM",
    "feeType": "EARLY_CHECKIN",
    "amount": 50.00,
    "calculationBasis": "PER_STAY",
    "isMandatory": false,
    "isRefundable": false
  },
  {
    "feeCode": "PET-FEE",
    "feeName": "Pet Accommodation",
    "description": "Per pet, per night",
    "feeType": "PET_FEE",
    "amount": 25.00,
    "calculationBasis": "PER_NIGHT",
    "isMandatory": false,
    "isRefundable": false
  }
]
```

## 5. Comprehensive Pricing Calculator

Integrated pricing engine combining all revenue components.

### Pricing Breakdown

```
Room Rate:           $200.00 × 3 nights = $600.00
Promotional Discount:                    - $60.00
                                         --------
Subtotal:                                $540.00

Standard Tax (10%):                      + $54.00
City Tax (3%):                           + $16.20
Tourist Tax ($2/night):                  +  $6.00
                                         --------
Total Taxes:                             + $76.20

Resort Fee ($35/night):                  +$105.00
Parking ($20/night):                     + $60.00
                                         --------
Total Fees:                              +$165.00

GRAND TOTAL:                             $781.20
```

### API Endpoint

```
GET /api/v1/pricing/calculate
```

**Parameters:**
- `roomId` - Room to price
- `checkIn` - Check-in date
- `checkOut` - Check-out date
- `numberOfGuests` - Guest count
- `city` - Location for city tax
- `promoCode` - Optional discount code

**Response:**
```json
{
  "roomBasePrice": 200.00,
  "numberOfNights": 3,
  "subtotal": 600.00,
  "discountAmount": 60.00,
  "discountDescription": "Spring Sale 10%",
  "taxAmount": 54.00,
  "cityTaxAmount": 22.20,
  "taxes": [
    {
      "taxName": "City Tax",
      "rate": 3.00,
      "amount": 16.20,
      "calculationType": "PERCENTAGE"
    },
    {
      "taxName": "Tourist Tax",
      "rate": 0,
      "amount": 6.00,
      "calculationType": "PER_NIGHT"
    }
  ],
  "extraFees": [
    {
      "feeName": "Resort Fee",
      "feeType": "RESORT_FEE",
      "amount": 105.00,
      "isMandatory": true,
      "isRefundable": false
    },
    {
      "feeName": "Parking",
      "feeType": "PARKING",
      "amount": 60.00,
      "isMandatory": false,
      "isRefundable": false
    }
  ],
  "totalFees": 165.00,
  "grandTotal": 781.20
}
```

## 6. Database Schema

### New Tables

#### `products`
- Product catalog with inventory
- SKU, name, description
- Category, price, cost
- Stock levels
- Upsell configuration
- Tax settings

#### `reservation_products`
- Products added to reservations
- Links products to bookings
- Tracks quantity and pricing
- Delivery status
- Upsell flag

#### `city_taxes`
- City/local tax configuration
- Multiple calculation methods
- Effective date ranges
- Location hierarchy

#### `extra_fees`
- Fee definitions
- Multiple calculation bases
- Mandatory/optional flags
- Room type applicability

#### `reservation_fees`
- Fees applied to reservations
- Links fees to bookings
- Tracks amounts
- Refund status

#### `upsell_offers`
- Upsell offer definitions
- Room upgrades, products, services
- Pricing and discounts
- Validity and usage limits
- Conversion tracking

## 7. Revenue Optimization Strategies

### Product Strategy
1. **Minibar Optimization**
   - Premium items with high margins
   - Seasonal selections
   - Local specialties

2. **Service Bundling**
   - Spa + Dining packages
   - Airport transfer + Welcome amenity
   - Romance packages

3. **Time-Based Pricing**
   - Early check-in premium
   - Late checkout fees
   - Peak season surcharges

### Upselling Best Practices

1. **Timing**
   - At booking: Room upgrades
   - Post-booking email: Services
   - Check-in: Last-minute offers
   - During stay: F&B, Spa

2. **Personalization**
   - Based on guest history
   - Loyalty tier benefits
   - Special occasions
   - Length of stay

3. **Presentation**
   - Visual appeal (images)
   - Clear value proposition
   - Limited time offers
   - Social proof (reviews)

### Tax Compliance

1. **Accurate Calculation**
   - Location-based rules
   - Rate updates
   - Exemption handling
   - Audit trails

2. **Transparency**
   - Clear breakdown
   - Show before booking
   - No hidden fees
   - Detailed receipts

### Fee Management

1. **Disclosure**
   - Display all fees upfront
   - Explain what's included
   - Highlight optional fees
   - Provide alternatives

2. **Competitive Analysis**
   - Monitor competitor fees
   - Adjust seasonally
   - Bundle for value
   - Loyalty exemptions

## 8. Frontend Integration

### Product Catalog UI
- Full CRUD management
- Category filtering
- Stock level indicators
- Upsell designation
- Quick stock updates

### Pricing Display
- Real-time calculation
- Interactive breakdown
- Show/hide details
- Compare options
- Apply discounts

### Upsell Presentation
- Modal overlays
- Carousel display
- One-click acceptance
- Decline tracking

### Admin Tools
- Revenue dashboard
- Popular products
- Conversion rates
- Tax reports
- Fee analysis

## 9. Business Intelligence

### Key Metrics

**Product Performance:**
- Revenue by category
- Best-selling items
- Profit margins
- Stock turnover
- Upsell conversion

**Upselling Metrics:**
- Offer acceptance rate
- Average upsell value
- Most successful offers
- Timing analysis
- Channel performance

**Tax & Fee Analysis:**
- Total tax collected
- City tax breakdown
- Fee revenue
- Refund rates
- Compliance status

### Reporting

```sql
-- Daily Revenue Summary
SELECT 
  DATE(created_at) as date,
  SUM(room_charges) as room_revenue,
  SUM(product_revenue) as product_revenue,
  SUM(tax_amount) as taxes,
  SUM(fee_amount) as fees,
  SUM(grand_total) as total_revenue
FROM reservations
WHERE status = 'COMPLETED'
GROUP BY DATE(created_at);

-- Top Products
SELECT 
  p.name,
  COUNT(rp.id) as orders,
  SUM(rp.quantity) as units_sold,
  SUM(rp.total_price) as revenue
FROM products p
JOIN reservation_products rp ON rp.product_id = p.id
WHERE rp.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY p.id
ORDER BY revenue DESC
LIMIT 10;

-- Upsell Performance
SELECT 
  uo.title,
  uo.offer_type,
  uo.used_count as accepted,
  (uo.used_count * uo.offer_price) as revenue_generated
FROM upsell_offers uo
WHERE uo.is_active = true
ORDER BY revenue_generated DESC;
```

## 10. Testing Examples

### Calculate Pricing
```bash
curl "http://localhost:8080/api/v1/pricing/calculate?\
roomId=1&\
checkIn=2024-03-15&\
checkOut=2024-03-18&\
numberOfGuests=2&\
city=Miami&\
promoCode=SPRING2024"
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "sku": "SPA-001",
    "name": "Massage 60min",
    "category": "SPA",
    "price": 120.00,
    "isUpsell": true,
    "upsellPriority": 10
  }'
```

### Get Upsell Recommendations
```bash
curl http://localhost:8080/api/v1/upsell/recommendations/123 \
  -H "Authorization: Bearer TOKEN"
```

## 11. Future Enhancements

1. **Dynamic Pricing**
   - AI-based room pricing
   - Demand forecasting
   - Competitor rate monitoring
   - Seasonal adjustments

2. **Advanced Upselling**
   - Machine learning recommendations
   - A/B testing offers
   - Real-time personalization
   - Cross-sell opportunities

3. **Product Features**
   - Vendor management
   - Purchase orders
   - Recipe management (F&B)
   - Expiry tracking

4. **Tax Automation**
   - Tax API integration
   - Automatic rate updates
   - Multi-jurisdiction support
   - Exemption certificates

---

**For implementation details, see:**
- Backend: `backend/src/main/java/com/hotel/management/`
- Frontend: `frontend/src/pages/admin/ProductsPage.tsx`
- API Documentation: `http://localhost:8080/swagger-ui.html`
