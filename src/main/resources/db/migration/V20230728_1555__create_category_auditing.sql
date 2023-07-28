create table revinfo
(
    rev      integer not null  primary key,
    revtstmp bigint
);

create sequence revinfo_seq
    increment by 1;

CREATE TABLE category_aud
(
    category_id   uuid NOT NULL,
    rev           integer NOT NULL REFERENCES REVINFO (REV),
    revtype       smallint,
    category_name varchar(255),
    created_at    timestamp(6),
    created_by    varchar(255),
    modified_at   timestamp(6),
    modified_by   varchar(255),
    PRIMARY KEY (rev, category_id)
);
