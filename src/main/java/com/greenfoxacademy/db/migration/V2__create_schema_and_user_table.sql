CREATE TABLE "user" (
  id            SERIAL PRIMARY KEY      NOT NULL,
  user_name     VARCHAR(255)            NOT NULL,
  user_password VARCHAR(255)            NOT NULL,
  user_role     VARCHAR(255)            NOT NULL
);

INSERT INTO "user" (id, user_name, user_password, user_role) VALUES (1, 'admin', 'admin', 'ADMIN');
SELECT setval('user_id_seq', (SELECT max("user".id)
                              FROM "user"));