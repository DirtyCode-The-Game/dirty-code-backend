-- V2: Alteração dos tipos de colunas para BigInteger no Java (NUMERIC no Postgres)

-- Tabela Avatar: Experiência
ALTER TABLE avatar ALTER COLUMN experience TYPE NUMERIC(38, 0);
ALTER TABLE avatar ALTER COLUMN total_experience TYPE NUMERIC(38, 0);
ALTER TABLE avatar ALTER COLUMN next_level_experience TYPE NUMERIC(38, 0);

-- Tabela Action: Experiência e Dano de Falha
ALTER TABLE action ALTER COLUMN xp TYPE NUMERIC(38, 0);
ALTER TABLE action ALTER COLUMN lost_hp_failure TYPE NUMERIC(38, 0);
