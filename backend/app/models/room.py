from sqlalchemy import Column, Integer, String, Float, Boolean, Text, ForeignKey, Table, Enum
from sqlalchemy.orm import relationship
import enum
from app.db.session import Base


class RoomStatus(str, enum.Enum):
    AVAILABLE = "available"
    OCCUPIED = "occupied"
    DIRTY = "dirty"
    CLEANING = "cleaning"
    INSPECTED = "inspected"
    OUT_OF_ORDER = "out_of_order"
    MAINTENANCE = "maintenance"


class BedType(str, enum.Enum):
    SINGLE = "single"
    DOUBLE = "double"
    QUEEN = "queen"
    KING = "king"
    TWIN = "twin"


# Association table for many-to-many relationship
room_amenities = Table(
    'room_amenities_association',
    Base.metadata,
    Column('room_id', Integer, ForeignKey('rooms.id')),
    Column('amenity_id', Integer, ForeignKey('room_amenities.id'))
)


class RoomType(Base):
    __tablename__ = "room_types"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(Text)
    base_price = Column(Float, nullable=False)
    capacity = Column(Integer, nullable=False)
    bed_type = Column(Enum(BedType))
    size_sqm = Column(Float)
    
    # Relationships
    rooms = relationship("Room", back_populates="room_type")


class Room(Base):
    __tablename__ = "rooms"
    
    id = Column(Integer, primary_key=True, index=True)
    room_number = Column(String(10), unique=True, nullable=False, index=True)
    room_type_id = Column(Integer, ForeignKey("room_types.id"), nullable=False)
    floor = Column(Integer)
    status = Column(Enum(RoomStatus), default=RoomStatus.AVAILABLE)
    
    # Features
    is_accessible = Column(Boolean, default=False)
    has_view = Column(Boolean, default=False)
    view_type = Column(String(50))  # ocean, city, garden, etc.
    is_smoking = Column(Boolean, default=False)
    
    # Maintenance
    last_maintenance_date = Column(String)
    notes = Column(Text)
    
    # Relationships
    room_type = relationship("RoomType", back_populates="rooms")
    amenities = relationship("RoomAmenity", secondary=room_amenities, back_populates="rooms")
    reservations = relationship("ReservationRoom", back_populates="room")
    housekeeping_tasks = relationship("HousekeepingTask", back_populates="room")


class RoomAmenity(Base):
    __tablename__ = "room_amenities"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(Text)
    icon = Column(String(50))  # icon name for frontend
    
    # Relationships
    rooms = relationship("Room", secondary=room_amenities, back_populates="amenities")
