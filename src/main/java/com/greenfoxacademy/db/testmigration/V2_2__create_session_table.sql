CREATE TABLE konnekt_test.session (
  token            VARCHAR(255)       NOT NULL,
  user_id     integer REFERENCES konnekt_test.user(id)
);