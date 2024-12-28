CREATE TABLE parties (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    point_of_contact VARCHAR(255),
    contact_number VARCHAR(255),
    email VARCHAR(255),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    state VARCHAR(255),
    district VARCHAR(255),
    taluka VARCHAR(255),
    city VARCHAR(255),
    pin_code VARCHAR(255),
    created_at BIGINT NOT NULL
);
