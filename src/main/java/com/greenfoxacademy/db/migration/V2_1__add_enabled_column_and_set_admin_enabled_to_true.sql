ALTER TABLE "user"
  ADD enabled BOOLEAN;

UPDATE "user"
SET enabled = TRUE
WHERE id = 1;
