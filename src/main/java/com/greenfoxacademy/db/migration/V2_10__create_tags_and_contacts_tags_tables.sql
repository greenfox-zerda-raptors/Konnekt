CREATE TABLE "tag" (
  id                  SERIAL PRIMARY KEY      NOT NULL,
  tag_name        VARCHAR(255)            NOT NULL
);

CREATE TABLE "contact_tags" (
  tags_id            INTEGER REFERENCES "tag" (id),
  contacts_id        INTEGER REFERENCES "contact" (id)
)