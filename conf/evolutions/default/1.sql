# Tasks schema

# --- !Ups

CREATE SEQUENCE sub_id_seq;

CREATE TABLE subscription (
    id integer NOT NULL DEFAULT nextval('sub_id_seq'),
    tag varchar(255),
    user_id integer
);

# --- !Downs

DROP TABLE subscription;
DROP SEQUENCE sub_id_seq;