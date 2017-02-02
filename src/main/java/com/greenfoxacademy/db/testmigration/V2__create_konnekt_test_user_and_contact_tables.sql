CREATE TABLE konnekt_test.user (
  id            SERIAL       NOT NULL,
  user_name     VARCHAR(255) NOT NULL,
  user_password VARCHAR(255) NOT NULL,
  user_role     VARCHAR(255) NOT NULL,
  CONSTRAINT "konnekt_test.user_id_seq" PRIMARY KEY (id)
);

INSERT INTO konnekt_test.user (id, user_name, user_password, user_role) VALUES (1, 'admin', 'admin', 'ADMIN');
SELECT setval('konnekt_test.user_id_seq', (SELECT max(konnekt_test.user.id)
                                      FROM konnekt_test.user));

CREATE TABLE konnekt_test.contact (
  id            SERIAL       NOT NULL,
  user_id     integer REFERENCES konnekt_test.user(id),
  contact_name VARCHAR(255) NOT NULL,
  contact_description   VARCHAR(255) NOT NULL,
  CONSTRAINT "konnekt_test.contact_id_seq" PRIMARY KEY (id)
);