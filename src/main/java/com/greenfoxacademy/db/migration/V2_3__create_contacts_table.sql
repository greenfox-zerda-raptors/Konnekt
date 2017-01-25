CREATE TABLE konnekt.contact (
  id            SERIAL       NOT NULL,
  user_id     integer REFERENCES konnekt.user(id),
  contact_name VARCHAR(255) NOT NULL,
  contact_description   VARCHAR(255) NOT NULL,
  CONSTRAINT "konnekt.contact_id_seq" PRIMARY KEY (id)
);