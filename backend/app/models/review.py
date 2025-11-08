from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Text, Boolean
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.db.session import Base


class Review(Base):
    __tablename__ = "reviews"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    reservation_id = Column(Integer, ForeignKey("reservations.id"))
    room_type_id = Column(Integer, ForeignKey("room_types.id"))
    
    # Ratings (1-5 scale)
    overall_rating = Column(Float, nullable=False)
    cleanliness_rating = Column(Float)
    comfort_rating = Column(Float)
    location_rating = Column(Float)
    service_rating = Column(Float)
    value_rating = Column(Float)
    
    # Review Content
    title = Column(String(200))
    comment = Column(Text)
    
    # Status
    is_verified = Column(Boolean, default=False)
    is_published = Column(Boolean, default=False)
    
    # Timestamps
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    # Relationships
    user = relationship("User", back_populates="reviews")
