ALTER TABLE avatars RENAME COLUMN life TO current_life;
ALTER TABLE avatars RENAME COLUMN stamina TO current_stamina;

ALTER TABLE avatars ADD COLUMN max_life INTEGER;
ALTER TABLE avatars ADD COLUMN max_stamina INTEGER;