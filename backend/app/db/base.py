# Import all models here for Alembic to detect
from app.db.session import Base
from app.models.user import User
from app.models.room import Room, RoomType, RoomAmenity
from app.models.reservation import Reservation, ReservationRoom, ReservationAddon
from app.models.payment import Payment, Refund
from app.models.housekeeping import HousekeepingTask, RoomStatus
from app.models.guest import Guest, GuestPreference
from app.models.review import Review
from app.models.loyalty import LoyaltyProgram, LoyaltyTransaction
from app.models.promotion import Promotion
from app.models.report import Report
