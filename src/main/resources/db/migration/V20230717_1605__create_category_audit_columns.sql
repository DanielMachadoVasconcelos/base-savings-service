ALTER TABLE savings.category
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN modified_at TIMESTAMPTZ,
    ADD COLUMN created_by VARCHAR,
    ADD COLUMN modified_by VARCHAR;