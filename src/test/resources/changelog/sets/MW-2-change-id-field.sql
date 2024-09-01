--liquibase formatted sql

--changeset an.e.ageev:2 runInTransaction:true

ALTER TABLE products ALTER COLUMN id TYPE INT;

--rollback ALTER TABLE products ALTER COLUMN id TYPE SERIAL;