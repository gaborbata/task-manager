CREATE INDEX IF NOT EXISTS `idx_user_id` ON `task` (user_id);

CREATE UNIQUE INDEX IF NOT EXISTS `idx_id_user_id` ON `task` (id, user_id);
