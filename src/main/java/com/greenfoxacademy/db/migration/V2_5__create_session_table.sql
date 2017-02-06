CREATE TABLE "session" (
  token   VARCHAR(255) NOT NULL,
  user_id INTEGER REFERENCES "user" (id)
);