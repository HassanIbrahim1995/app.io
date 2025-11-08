-- Create databases for each microservice
CREATE DATABASE user_db;
CREATE DATABASE guest_db;
CREATE DATABASE room_db;
CREATE DATABASE reservation_db;
CREATE DATABASE payment_db;
CREATE DATABASE housekeeping_db;
CREATE DATABASE product_db;
CREATE DATABASE policy_db;
CREATE DATABASE review_db;
CREATE DATABASE analytics_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE user_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE guest_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE room_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE reservation_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE housekeeping_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE product_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE policy_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE review_db TO hotel_admin;
GRANT ALL PRIVILEGES ON DATABASE analytics_db TO hotel_admin;
