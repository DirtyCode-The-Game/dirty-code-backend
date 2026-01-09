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
    stamina INTEGER,
    life INTEGER,
    money DECIMAL(19, 2),
    available_points INTEGER,
    intelligence INTEGER,
    charisma INTEGER,
    strength INTEGER,
    stealth INTEGER,
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

INSERT INTO actions (id, type, title, description, stamina, money, money_variation, xp, xp_variation, required_strength, required_intelligence, required_charisma, required_stealth, can_be_arrested, lost_hp_failure, lost_hp_failure_variation, text_file, action_image, failure_chance, created_at, updated_at)
VALUES 
('11111111-1111-1111-1111-111111111111', 'hacking', 'Urubu do Pix', 'Me mande 10 dinheiros que eu te devolvo 1000 em 24h', -25, 50, 0.5, 20, 0.5, 2, 3, 4, 1, true, 0, 0, 'urubu_do_pix.json', 'urubu_do_pix.jpg', 0.2,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'training', 'Video do deschampo', 'Blalbalbalbla', -10, -10, 0.0, 10, 0.5, 0, 0, 0, 0, false, 0, 0, 'deschampo.json', NULL, 0, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', 'work', 'Ajustar canais de tv do pai', 'O pai esta desesperado, a tv não esta funcionando', -10, 10, 0.5, 10, 0.5, 0, 0, 0, 0, false, 5, 0.2, 'tv_do_pai.json', 'tv_do_pai.jpg', 0.2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO users (id, firebase_uid, name, email, photo_url, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'mock-token-123', 'Mock User', 'mock-user@example.com', 'http://example.com/photo.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
