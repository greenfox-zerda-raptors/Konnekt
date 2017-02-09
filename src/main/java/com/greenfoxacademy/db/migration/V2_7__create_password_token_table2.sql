CREATE TABLE "password_token" (
  token     VARCHAR(255) PRIMARY KEY NOT NULL,
  user_id   INTEGER REFERENCES "user" (id),
  timestamp TIMESTAMP,
  valid     TIMESTAMP
);