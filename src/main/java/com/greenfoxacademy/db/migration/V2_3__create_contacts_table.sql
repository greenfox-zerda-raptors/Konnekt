CREATE TABLE "contact" (
  id                  SERIAL PRIMARY KEY      NOT NULL,
  user_id             INTEGER REFERENCES "user" (id),
  contact_name        VARCHAR(255)            NOT NULL,
  contact_description VARCHAR(255)            NOT NULL
);