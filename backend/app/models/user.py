from sqlalchemy import Column, Integer, String, Boolean, DateTime, Enum
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
import enum
from app.db.session import Base


class UserRole(str, enum.Enum):
    GUEST = "guest"
    FRONT_DESK = "front_desk"
    HOUSEKEEPING = "housekeeping"
    MANAGER = "manager"
    ADMIN = "admin"


class User(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String, unique=True, index=True, nullable=False)
    username = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=False)
    full_name = Column(String)
    phone = Column(String)
    role = Column(Enum(UserRole), default=UserRole.GUEST, nullable=False)
    is_active = Column(Boolean, default=True)
    is_superuser = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    # Relationships
    guest_profile = relationship("Guest", back_populates="user", uselist=False)
    reservations = relationship("Reservation", back_populates="user")
    reviews = relationship("Review", back_populates="user")
