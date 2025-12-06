BEGIN;

-- User Table
CREATE TABLE users(
	user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	username VARCHAR(254) UNIQUE NOT NULL,
	email VARCHAR(254) UNIQUE NOT NULL,
	password_hash VARCHAR(500) NOT NULL,
	role_name VARCHAR(50) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Location Table
CREATE TABLE locations(
	location_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	location_name VARCHAR UNIQUE NOT NULL,
	address VARCHAR UNIQUE,
	city VARCHAR NOT NULL,
	country VARCHAR NOT NULL
);

-- Beverage Table
CREATE TABLE beverages(
	beverage_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	beverage_name VARCHAR(150) NOT NULL,
	"type" VARCHAR NOT NULL,
	abv DECIMAL(5,2) NOT NULL,
	description TEXT,
	image_url VARCHAR(500),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Review Table
CREATE TABLE reviews(
	review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL,
	beverage_id UUID NOT NULL,
	location_id UUID NOT NULL,
	rating DECIMAL(2,1) CHECK (rating BETWEEN 0 AND 5),	-- rating will be 0-5, with 0.5 increments
	notes TEXT, 	-- I'm not sure if this column is needed since beverage already
	-- has a description, which I presume is the same as notes 
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (beverage_id) REFERENCES beverages(beverage_id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(location_id) ON DELETE CASCADE
);

COMMIT;