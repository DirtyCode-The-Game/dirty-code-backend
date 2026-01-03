CREATE TABLE users (
    id UUID PRIMARY KEY,
    firebase_uid VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    photo_url TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE avatars (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255),
    picture TEXT,
    level INTEGER,
    experience INTEGER,
    stamina INTEGER,
    life INTEGER,
    money DECIMAL(19, 2),
    available_points INTEGER,
    intelligence INTEGER,
    charisma INTEGER,
    street_intelligence INTEGER,
    stealth INTEGER,
    active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_avatars_user FOREIGN KEY (user_id) REFERENCES users(id)
);
