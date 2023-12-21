CREATE TABLE IF NOT EXISTS member
(
    member_id       UUID         NOT NULL DEFAULT gen_random_uuid(),
    member_name     VARCHAR(255) NOT NULL,
    member_email    BYTEA,
    CONSTRAINT pk_member PRIMARY KEY (member_id),
    CONSTRAINT uk_member_email UNIQUE (member_email)
);

ALTER TABLE member
    ADD COLUMN created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN modified_at TIMESTAMPTZ,
    ADD COLUMN created_by  VARCHAR,
    ADD COLUMN modified_by VARCHAR;

ALTER TABLE member ALTER COLUMN member_name TYPE VARCHAR COLLATE "pg_catalog"."C";

create sequence member_revision_seq INCREMENT BY 1 START WITH 1;

create table member_revision
(
    rev      BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('member_revision_seq'),
    revtstmp BIGINT
);

ALTER sequence member_revision_seq OWNED BY member_revision.rev;

CREATE TABLE member_aud
(
    member_id   uuid   NOT NULL,
    rev         BIGINT NOT NULL REFERENCES member_revision (rev),
    revtype     smallint,
    member_name varchar(255),
    member_email BYTEA,
    created_at  timestamp(6),
    created_by  varchar(255),
    modified_at timestamp(6),
    modified_by varchar(255),
    PRIMARY KEY (rev, member_id)
);
