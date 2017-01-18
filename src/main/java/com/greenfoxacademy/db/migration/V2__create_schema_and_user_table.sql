CREATE TABLE konnekt.user(
  user_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  user_name VARCHAR(255) NOT NULL,
  user_password VARCHAR(255) NOT NULL,
  user_role VARCHAR(255) NOT NULL
);

INSERT INTO konnekt.user VALUES(1, 'admin', 'admin' , 'ADMIN');