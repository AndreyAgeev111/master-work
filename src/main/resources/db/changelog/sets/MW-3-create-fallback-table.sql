--liquibase formatted sql

--changeset an.e.ageev:3 runInTransaction:true

CREATE TABLE IF NOT EXISTS dead_letter_events
(
    id              INT PRIMARY KEY,
    payload         JSON,
    create_date     DATE,
    update_date     DATE,
    current_attempt INT
);

--rollback DROP TABLE dead_letter_events;