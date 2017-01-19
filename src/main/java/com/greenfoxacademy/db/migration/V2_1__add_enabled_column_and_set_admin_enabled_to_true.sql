ALTER TABLE konnekt.user
ADD enabled BOOLEAN;

UPDATE konnekt.user
SET enabled=true
WHERE user_id = 1;
