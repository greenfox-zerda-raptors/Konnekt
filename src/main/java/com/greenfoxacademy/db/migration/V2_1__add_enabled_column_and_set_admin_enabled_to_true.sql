ALTER TABLE konnekt.user
  ADD enabled BOOLEAN;

UPDATE konnekt.user
SET enabled = TRUE
WHERE user_id = 1;
