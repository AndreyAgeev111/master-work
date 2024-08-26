--liquibase formatted sql

--changeset an.e.ageev:1 runInTransaction:true

CREATE TABLE IF NOT EXISTS products (
    id     SERIAL       PRIMARY KEY,
    name   VARCHAR      NOT NULL,
    price INT NOT NULL,
    is_available BOOLEAN,
    description TEXT
    );

--rollback DROP TABLE products;