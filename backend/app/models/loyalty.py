from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Text, Enum
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
import enum
from app.db.session import Base


class TransactionType(str, enum.Enum):
    EARNED = "earned"
    REDEEMED = "redeemed"
    EXPIRED = "expired"
    ADJUSTED = "adjusted"


class LoyaltyProgram(Base):
    __tablename__ = "loyalty_programs"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(Text)
    
    # Tier Configuration
    tier_name = Column(String(50), nullable=False)
    min_points = Column(Integer, nullable=False)
    discount_percentage = Column(Float, default=0.0)
    
    # Benefits
    benefits = Column(Text)  # JSON string of benefits


class LoyaltyTransaction(Base):
    __tablename__ = "loyalty_transactions"
    
    id = Column(Integer, primary_key=True, index=True)
    guest_id = Column(Integer, ForeignKey("guests.id"), nullable=False)
    reservation_id = Column(Integer, ForeignKey("reservations.id"))
    
    # Transaction Details
    points = Column(Integer, nullable=False)
    transaction_type = Column(Enum(TransactionType), nullable=False)
    description = Column(Text)
    
    # Balance
    balance_after = Column(Integer)
    
    # Timestamps
    created_at = Column(DateTime(timezone=True), server_default=func.now())
