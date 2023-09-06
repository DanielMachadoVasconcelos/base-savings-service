CREATE TABLE IF NOT EXISTS category
(
    category_id   UUID NOT NULL DEFAULT gen_random_uuid(),
    category_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (category_id),
    CONSTRAINT uk_category_name UNIQUE (category_name)
);
