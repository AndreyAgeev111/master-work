--liquibase formatted sql

--changeset an.e.ageev:5 runInTransaction:true

ALTER TABLE dead_letter_events ALTER COLUMN create_date TYPE TIMESTAMP;
ALTER TABLE dead_letter_events ALTER COLUMN update_date TYPE TIMESTAMP;

--rollback ALTER TABLE dead_letter_events ALTER COLUMN create_date TYPE DATE; ALTER TABLE dead_letter_events ALTER COLUMN update_date TYPE DATE;