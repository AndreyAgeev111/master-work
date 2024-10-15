--liquibase formatted sql

--changeset an.e.ageev:7 runInTransaction:true

ALTER TABLE products ALTER COLUMN id DROP DEFAULT;

--rollback ALTER TABLE products ALTER COLUMN id DROP DEFAULT;