--liquibase formatted sql

--changeset an.e.ageev:6 runInTransaction:true

ALTER TABLE dead_letter_events ALTER COLUMN payload TYPE TEXT;

--rollback ALTER TABLE dead_letter_events ALTER COLUMN payload TYPE JSON;