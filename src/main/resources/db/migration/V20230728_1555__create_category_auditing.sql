create sequence category_revision_seq INCREMENT BY 1 START WITH 1;

create table category_revision
(
    rev      BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('category_revision_seq'),
    revtstmp BIGINT
);

ALTER sequence category_revision_seq OWNED BY category_revision.rev;

CREATE TABLE category_aud
(
    category_id   uuid NOT NULL,
    rev           BIGINT NOT NULL REFERENCES category_revision(rev),
    revtype       smallint,
    category_name varchar(255),
    created_at    timestamp(6),
    created_by    varchar(255),
    modified_at   timestamp(6),
    modified_by   varchar(255),
    PRIMARY KEY (rev, category_id)
);
