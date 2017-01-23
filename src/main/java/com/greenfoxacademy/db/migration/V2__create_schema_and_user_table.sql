CREATE TABLE konnekt.user (
  id            SERIAL       NOT NULL,
  user_name     VARCHAR(255) NOT NULL,
  user_password VARCHAR(255) NOT NULL,
  user_role     VARCHAR(255) NOT NULL,
  CONSTRAINT "konnekt.user_id_seq" PRIMARY KEY (id)
);

INSERT INTO konnekt.user (id, user_name, user_password, user_role) VALUES (1, 'admin', 'admin', 'ADMIN');
SELECT setval('konnekt.user_id_seq', (SELECT max(konnekt.user.id)
                                      FROM konnekt.user));