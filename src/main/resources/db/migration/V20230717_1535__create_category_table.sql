CREATE SCHEMA IF NOT EXISTS savings;

CREATE TABLE IF NOT EXISTS savings.category
(
    category_id   UUID NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (category_id),
    CONSTRAINT uk_category_name UNIQUE (category_name)
);
