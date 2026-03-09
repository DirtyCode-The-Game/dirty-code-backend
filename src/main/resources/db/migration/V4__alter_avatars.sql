ALTER TABLE avatar RENAME COLUMN life TO current_life;
ALTER TABLE avatar RENAME COLUMN stamina TO current_stamina;

ALTER TABLE avatar ADD COLUMN max_life INTEGER;
ALTER TABLE avatar ADD COLUMN max_stamina INTEGER;