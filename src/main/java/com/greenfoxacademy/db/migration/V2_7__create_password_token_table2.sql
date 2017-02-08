CREATE TABLE "password_token" (
  token   VARCHAR(255) NOT NULL,
  user_id INTEGER REFERENCES "user" (id)
);