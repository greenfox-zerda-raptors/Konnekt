CREATE SEQUENCE konnekt.user_user_id_seq;
CREATE TABLE konnekt.user (
  user_id       SERIAL PRIMARY KEY NOT NULL,
  user_name     VARCHAR(255)       NOT NULL,
  user_password VARCHAR(255)       NOT NULL,
  user_role     VARCHAR(255)       NOT NULL
);

INSERT INTO konnekt.user (user_id, user_name, user_password, user_role) VALUES (1, 'admin', 'admin', 'ADMIN');
