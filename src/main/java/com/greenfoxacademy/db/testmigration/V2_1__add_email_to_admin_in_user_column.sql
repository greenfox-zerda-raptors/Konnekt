ALTER TABLE konnekt_test.user
  ADD email VARCHAR(255);

UPDATE konnekt_test.user
  SET email = 'admin@admin.hu'
  WHERE id = 1;