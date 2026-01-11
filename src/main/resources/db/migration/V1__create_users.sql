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
    story VARCHAR(200),
    level INTEGER,
    experience INTEGER,
    total_experience INTEGER DEFAULT 0,
    next_level_experience INTEGER,
    stamina INTEGER,
    life INTEGER,
    money DECIMAL(19, 2),
    available_points INTEGER,
    intelligence INTEGER,
    charisma INTEGER,
    strength INTEGER,
    stealth INTEGER,
    hacking INTEGER DEFAULT 0,
    work INTEGER DEFAULT 0,
    timeout TIMESTAMP,
    timeout_type VARCHAR(50),
    active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_avatars_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE actions (
    id UUID PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    stamina INTEGER,
    hp INTEGER,
    hp_variation DOUBLE PRECISION,
    money DECIMAL(19, 2),
    money_variation DOUBLE PRECISION,
    xp INTEGER,
    xp_variation DOUBLE PRECISION,
    required_strength INTEGER,
    required_intelligence INTEGER,
    required_charisma INTEGER,
    required_stealth INTEGER,
    can_be_arrested BOOLEAN,
    lost_hp_failure INTEGER,
    lost_hp_failure_variation DOUBLE PRECISION,
    text_file VARCHAR(255),
    action_image VARCHAR(255),
    failure_chance DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

INSERT INTO users (id, firebase_uid, name, email, photo_url, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'mock-token-123', 'Mock User', 'mock-user@example.com', 'http://example.com/photo.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
