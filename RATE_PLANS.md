# Rate Plans System

## Overview
The hotel management system includes a comprehensive rate plan feature that allows for different pricing strategies with varying cancellation policies and amenities.

## Rate Plan Types

### 1. **STANDARD**
- Regular rate with standard cancellation policy
- Balanced between price and flexibility
- Full refund up to 48 hours before check-in

### 2. **FLEXIBLE**
- Higher price but maximum flexibility
- Full refund up to 24 hours before check-in
- Best for uncertain travel plans

### 3. **NON_REFUNDABLE**
- Lowest price available
- No refunds under any circumstances
- Best for confirmed travel plans
- Typically 15-30% cheaper than standard rates

### 4. **ADVANCE_PURCHASE**
- Book early, save more
- Requires booking X days in advance (e.g., 14, 21, 30 days)
- Limited refund options
- 10-25% discount

### 5. **LAST_MINUTE**
- Special deals for same-day or next-day bookings
- Limited availability
- Flexible cancellation typically not available

### 6. **CORPORATE**
- Special rates for corporate accounts
- May require company code
- Often includes breakfast and late checkout

### 7. **GOVERNMENT**
- Discounted rates for government employees
- Requires valid government ID
- Tax exemptions may apply

### 8. **MEMBER_EXCLUSIVE**
- Exclusive rates for loyalty program members
- Tier-based pricing (Bronze, Silver, Gold, Platinum)
- Additional perks included

### 9. **PACKAGE**
- Bundled rates with extras
- May include breakfast, spa credits, parking, etc.
- Better value than individual purchases

### 10. **PROMOTIONAL**
- Special promotional rates for marketing campaigns
- Time-limited offers
- May have special conditions

## Cancellation Policy Types

### 1. **FLEXIBLE**
- Full refund up to 24 hours before check-in
- No penalty if cancelled in time
- Small fee after deadline

### 2. **MODERATE**
- Full refund up to 5 days before check-in
- 50% refund between 5-2 days
- No refund within 48 hours

### 3. **STRICT**
- 50% refund up to 7 days before check-in
- 25% refund between 7-3 days
- No refund within 72 hours

### 4. **NON_REFUNDABLE**
- No refund under any circumstances
- Lowest price point
- Non-transferable

### 5. **SUPER_FLEXIBLE**
- Full refund up to check-in time
- Maximum flexibility
- Premium pricing

### 6. **LONG_TERM_FLEXIBLE**
- Full refund up to 30 days before check-in
- For long-term or event bookings
- Graduated refund scale after 30 days

## Key Features

### Pricing Modifiers
- **Base Price Modifier**: Multiplier applied to base price (e.g., 0.85 for 15% off)
- **Percentage Discount**: Direct percentage discount (e.g., 20% off)
- **Fixed Discount**: Fixed amount discount (e.g., $50 off)

### Stay Requirements
- **Minimum Nights**: Minimum stay duration
- **Maximum Nights**: Maximum stay duration
- **Advance Booking Days**: Must book X days in advance

### Inclusions
- **Breakfast**: Complimentary breakfast included
- **WiFi**: Free internet access
- **Parking**: Free parking included
- **Early Check-in**: Allowed without extra charge
- **Late Checkout**: Allowed without extra charge

### Restrictions
- **Valid From/To**: Date range for rate plan validity
- **Blackout Dates**: Dates when plan is not available
- **Membership Required**: Loyalty membership required
- **Minimum Loyalty Tier**: Required loyalty level
- **Max Occupancy**: Maximum guests allowed

## API Endpoints

### Create Rate Plan
```http
POST /api/v1/rate-plans
Authorization: Bearer {token}
Content-Type: application/json

{
  "planCode": "NR-STANDARD",
  "name": "Non-Refundable Standard Rate",
  "description": "Save 20% with our non-refundable rate",
  "planType": "NON_REFUNDABLE",
  "cancellationPolicy": "NON_REFUNDABLE",
  "basePriceModifier": 0.80,
  "percentageDiscount": 0,
  "fixedDiscount": 0,
  "minimumNights": 1,
  "isRefundable": false,
  "includesBreakfast": false,
  "includesWifi": true,
  "includesParking": false,
  "isActive": true,
  "isVisibleToPublic": true,
  "cancellationPolicyDetails": "This rate is non-refundable. No changes or cancellations allowed.",
  "termsAndConditions": "Full payment required at booking. No refunds."
}
```

### Get Active Public Rate Plans
```http
GET /api/v1/rate-plans
```

### Get Rate Plans by Type
```http
GET /api/v1/rate-plans/type/NON_REFUNDABLE
GET /api/v1/rate-plans/type/FLEXIBLE
```

### Get Refundable/Non-Refundable Plans
```http
GET /api/v1/rate-plans/refundable
GET /api/v1/rate-plans/non-refundable
```

### Calculate Price with Rate Plan
```http
GET /api/v1/rate-plans/{id}/calculate-price?basePrice=100&numberOfNights=3
```

### Validate Rate Plan for Dates
```http
GET /api/v1/rate-plans/{id}/validate?checkIn=2025-01-15&checkOut=2025-01-18
```

### Check Cancellation Eligibility
```http
GET /api/v1/rate-plans/{id}/can-cancel?checkInDate=2025-01-15
```

## Example Rate Plans

### Example 1: Non-Refundable Standard
```json
{
  "planCode": "NR-STD",
  "name": "Non-Refundable Standard",
  "planType": "NON_REFUNDABLE",
  "basePriceModifier": 0.80,
  "isRefundable": false,
  "cancellationPolicy": "NON_REFUNDABLE",
  "includesBreakfast": false,
  "minimumNights": 1
}
```

### Example 2: Flexible Premium
```json
{
  "planCode": "FLEX-PREM",
  "name": "Flexible Premium Rate",
  "planType": "FLEXIBLE",
  "basePriceModifier": 1.15,
  "isRefundable": true,
  "cancellationPolicy": "SUPER_FLEXIBLE",
  "cancellationDeadlineHours": 0,
  "includesBreakfast": true,
  "includesWifi": true,
  "lateCheckoutAllowed": true
}
```

### Example 3: Advance Purchase 21
```json
{
  "planCode": "ADV-21",
  "name": "Advance Purchase 21 Days",
  "planType": "ADVANCE_PURCHASE",
  "basePriceModifier": 0.85,
  "advanceBookingDays": 21,
  "isRefundable": true,
  "cancellationPolicy": "MODERATE",
  "cancellationDeadlineHours": 120,
  "minimumNights": 2
}
```

### Example 4: Member Exclusive Gold
```json
{
  "planCode": "MEM-GOLD",
  "name": "Gold Member Exclusive",
  "planType": "MEMBER_EXCLUSIVE",
  "basePriceModifier": 0.90,
  "isRefundable": true,
  "cancellationPolicy": "FLEXIBLE",
  "requiresMembership": true,
  "minimumLoyaltyTier": "GOLD",
  "includesBreakfast": true,
  "includesWifi": true,
  "includesParking": true,
  "earlyCheckinAllowed": true,
  "lateCheckoutAllowed": true
}
```

## Integration with Reservations

When creating a reservation, include the rate plan ID:

```json
{
  "roomId": 1,
  "ratePlanId": 5,
  "checkInDate": "2025-01-15",
  "checkOutDate": "2025-01-18",
  "numberOfGuests": 2
}
```

The system will:
1. Validate the rate plan is active and valid for the dates
2. Calculate the price using the rate plan modifiers
3. Apply the cancellation policy to the reservation
4. Track inclusions (breakfast, parking, etc.)

## Benefits

### For Hotel
- Maximize revenue with dynamic pricing
- Fill inventory with non-refundable rates
- Attract early bookers with advance purchase rates
- Reward loyalty members
- Clear cancellation policies reduce disputes

### For Guests
- Choose the right balance of price vs flexibility
- Save money with non-refundable rates
- Get more value with package rates
- Enjoy member benefits
- Understand cancellation terms upfront

## Best Practices

1. **Always offer at least 3 rate plans**: Standard, Flexible, Non-Refundable
2. **Price spread**: Non-refundable should be 15-25% cheaper than flexible
3. **Clear communication**: Explicitly state refund policies
4. **Smart defaults**: Standard rate as default, highlight savings on other plans
5. **Seasonal adjustments**: Update rate plans for peak/off-peak seasons
6. **A/B testing**: Test different pricing modifiers to optimize revenue
7. **Blackout dates**: Block rate plans during major events if needed
