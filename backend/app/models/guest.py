from sqlalchemy import Column, Integer, String, Date, ForeignKey, Text, JSON
from sqlalchemy.orm import relationship
from app.db.session import Base


class Guest(Base):
    __tablename__ = "guests"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True)
    
    # Personal Information
    date_of_birth = Column(Date)
    nationality = Column(String(100))
    passport_number = Column(String(50))
    address = Column(Text)
    city = Column(String(100))
    country = Column(String(100))
    postal_code = Column(String(20))
    
    # Preferences
    room_preferences = Column(JSON)  # bed type, floor, view, etc.
    dietary_restrictions = Column(Text)
    
    # Loyalty
    loyalty_tier = Column(String(20), default="bronze")
    loyalty_points = Column(Integer, default=0)
    
    # Emergency Contact
    emergency_contact_name = Column(String(100))
    emergency_contact_phone = Column(String(20))
    
    # Relationships
    user = relationship("User", back_populates="guest_profile")
    preferences = relationship("GuestPreference", back_populates="guest")


class GuestPreference(Base):
    __tablename__ = "guest_preferences"
    
    id = Column(Integer, primary_key=True, index=True)
    guest_id = Column(Integer, ForeignKey("guests.id"))
    preference_key = Column(String(50), nullable=False)
    preference_value = Column(String(200))
    
    # Relationships
    guest = relationship("Guest", back_populates="preferences")
