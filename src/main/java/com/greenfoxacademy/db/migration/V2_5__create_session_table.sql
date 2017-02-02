CREATE TABLE konnekt.session (
  token            VARCHAR(255)       NOT NULL,
  user_id     integer REFERENCES konnekt.user(id)
);