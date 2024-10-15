--liquibase formatted sql

--changeset an.e.ageev:4 runInTransaction:true

CREATE EXTENSION IF NOT EXISTS hstore;

--rollback DROP EXTENSION hstore;