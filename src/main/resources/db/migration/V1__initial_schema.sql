CREATE TABLE dirty_user (
    id UUID PRIMARY KEY,
    firebase_uid VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    photo_url TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE avatar (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255),
    picture TEXT,
    story TEXT,
    level INTEGER DEFAULT 1,
    experience INTEGER DEFAULT 0,
    total_experience INTEGER DEFAULT 0,
    next_level_experience INTEGER,
    stamina INTEGER DEFAULT 100,
    life INTEGER DEFAULT 100,
    money DECIMAL(19, 2) DEFAULT 0.0,
    available_points INTEGER DEFAULT 0,
    intelligence INTEGER DEFAULT 0,
    charisma INTEGER DEFAULT 0,
    strength INTEGER DEFAULT 0,
    stealth INTEGER DEFAULT 0,
    temporary_strength INTEGER DEFAULT 0,
    temporary_intelligence INTEGER DEFAULT 0,
    temporary_charisma INTEGER DEFAULT 0,
    temporary_stealth INTEGER DEFAULT 0,
    status_cooldown TIMESTAMP,
    hacking INTEGER DEFAULT 0,
    work INTEGER DEFAULT 0,
    timeout TIMESTAMP,
    timeout_type VARCHAR(50),
    active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_avatar_user FOREIGN KEY (user_id) REFERENCES dirty_user(id)
);

CREATE TABLE action (
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
    recommended_max_level INTEGER,
    temporary_strength INTEGER,
    temporary_intelligence INTEGER,
    temporary_charisma INTEGER,
    temporary_stealth INTEGER,
    action_cooldown TIMESTAMP,
    special_action VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE avatar_action_purchase (
    id UUID PRIMARY KEY,
    avatar_id UUID NOT NULL,
    action_id UUID NOT NULL,
    purchase_count INTEGER NOT NULL DEFAULT 0,
    current_price DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_purchase_avatar FOREIGN KEY (avatar_id) REFERENCES avatar(id),
    CONSTRAINT fk_purchase_action FOREIGN KEY (action_id) REFERENCES action(id),
    CONSTRAINT unique_avatar_action UNIQUE (avatar_id, action_id)
);

CREATE TABLE avatar_special_action (
    avatar_id UUID PRIMARY KEY,
    dr_strange_visible BOOLEAN DEFAULT FALSE,
    dr_strange_last_update TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_special_action_avatar FOREIGN KEY (avatar_id) REFERENCES avatar(id)
);

INSERT INTO dirty_user (id, firebase_uid, name, email, photo_url, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'mock-token-123', 'Mock DirtyUser', 'mock-user@example.com', 'http://example.com/photo.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
