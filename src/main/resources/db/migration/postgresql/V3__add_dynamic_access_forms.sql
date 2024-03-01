ALTER TABLE ACCESS_CRITERIA
    DROP CONSTRAINT CONSTRAINT_8F;

ALTER TABLE ACCESS_CRITERIA
    DROP CONSTRAINT CONSTRAINT_8F4;

ALTER TABLE ACCESS_CRITERIA_SECTION_LINK
    DROP CONSTRAINT CONSTRAINT_C06;

ALTER TABLE ACCESS_CRITERIA_SECTION_LINK
    DROP CONSTRAINT CONSTRAINT_C060;

ALTER TABLE ACCESS_CRITERIA_SECTION
    DROP CONSTRAINT CONSTRAINT_DA;

ALTER TABLE ACCESS_CRITERIA_SECTION
    DROP CONSTRAINT CONSTRAINT_DAC;

ALTER TABLE ACCESS_CRITERIA_SET
    DROP CONSTRAINT CONSTRAINT_E8;

ALTER TABLE ACCESS_CRITERIA_SET
    DROP CONSTRAINT CONSTRAINT_E8C;

ALTER TABLE RESOURCE
    DROP CONSTRAINT FK1XA946OABSGLYYF25U09D0NUU;

ALTER TABLE ACCESS_CRITERIA_SECTION_LINK
    DROP CONSTRAINT FKCGJ2EXTN02C91Q2LD1XVP54DI;

ALTER TABLE ACCESS_CRITERIA_SECTION_LINK
    DROP CONSTRAINT FKHWQK0A4NXT3P18E97L4LLG4BS;

ALTER TABLE ACCESS_CRITERIA_SECTION
    DROP CONSTRAINT FKIJWHM9GJJ8QDO5EU09IIBXDBO;

ALTER TABLE ACCESS_CRITERIA
    DROP CONSTRAINT FKT4TRPXMXHENAXG0I4DUGGIOCR;

CREATE SEQUENCE access_form_element_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE access_form_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE access_form_section_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE access_form_section_link_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE section_element_link_id_seq START WITH 100 INCREMENT BY 50;

CREATE TABLE access_form
(
    id            BIGINT NOT NULL,
    creation_date TIMESTAMP,
    modified_date TIMESTAMP,
    created_by    BIGINT,
    modified_by   BIGINT,
    name          VARCHAR(255),
    CONSTRAINT pk_accessform PRIMARY KEY (id)
);

CREATE TABLE access_form_element
(
    id                     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    creation_date          TIMESTAMP,
    modified_date          TIMESTAMP,
    created_by             BIGINT,
    modified_by            BIGINT,
    name                   VARCHAR(255)                            NOT NULL,
    label                  VARCHAR(255)                            NOT NULL,
    description            VARCHAR(255)                            NOT NULL,
    type                   VARCHAR(255)                            NOT NULL,
    access_form_section_id BIGINT,
    CONSTRAINT pk_accessformelement PRIMARY KEY (id)
);

CREATE TABLE access_form_section
(
    id            BIGINT       NOT NULL,
    creation_date TIMESTAMP,
    modified_date TIMESTAMP,
    created_by    BIGINT,
    modified_by   BIGINT,
    name          VARCHAR(255) NOT NULL,
    label         VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_accessformsection PRIMARY KEY (id)
);

CREATE TABLE access_form_section_element_link
(
    id                          BIGINT  NOT NULL,
    access_form_section_link_id BIGINT,
    access_form_element_id      BIGINT,
    is_required                 BOOLEAN NOT NULL,
    element_order               INT     NOT NULL,
    CONSTRAINT pk_accessformsectionelementlink PRIMARY KEY (id)
);

CREATE TABLE access_form_section_link
(
    id                     BIGINT NOT NULL,
    access_form_id         BIGINT,
    access_form_section_id BIGINT,
    section_order          INT    NOT NULL,
    CONSTRAINT pk_accessformsectionlink PRIMARY KEY (id)
);

ALTER TABLE resource
    ADD access_form_id BIGINT;

ALTER TABLE access_form_section_link
    ADD CONSTRAINT uc_accessformsectionlink_acfoidacfoseid UNIQUE (access_form_id, access_form_section_id);

ALTER TABLE access_form_element
    ADD CONSTRAINT FK_ACCESSFORMELEMENT_ON_ACCESS_FORM_SECTION FOREIGN KEY (access_form_section_id) REFERENCES access_form_section (id);

ALTER TABLE access_form_element
    ADD CONSTRAINT FK_ACCESSFORMELEMENT_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES person (id);

ALTER TABLE access_form_element
    ADD CONSTRAINT FK_ACCESSFORMELEMENT_ON_MODIFIED_BY FOREIGN KEY (modified_by) REFERENCES person (id);

ALTER TABLE access_form_section_element_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONELEMENTLINK_ON_ACCESS_FORM_ELEMENT FOREIGN KEY (access_form_element_id) REFERENCES access_form_element (id);

ALTER TABLE access_form_section_element_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONELEMENTLINK_ON_ACCESS_FORM_SECTION_LINK FOREIGN KEY (access_form_section_link_id) REFERENCES access_form_section_link (id);

ALTER TABLE access_form_section_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONLINK_ON_ACCESS_FORM FOREIGN KEY (access_form_id) REFERENCES access_form (id);

ALTER TABLE access_form_section_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONLINK_ON_ACCESS_FORM_SECTION FOREIGN KEY (access_form_section_id) REFERENCES access_form_section (id);

ALTER TABLE access_form_section
    ADD CONSTRAINT FK_ACCESSFORMSECTION_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES person (id);

ALTER TABLE access_form_section
    ADD CONSTRAINT FK_ACCESSFORMSECTION_ON_MODIFIED_BY FOREIGN KEY (modified_by) REFERENCES person (id);

ALTER TABLE access_form
    ADD CONSTRAINT FK_ACCESSFORM_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES person (id);

ALTER TABLE access_form
    ADD CONSTRAINT FK_ACCESSFORM_ON_MODIFIED_BY FOREIGN KEY (modified_by) REFERENCES person (id);

ALTER TABLE resource
    ADD CONSTRAINT FK_RESOURCE_ON_ACCESS_FORM FOREIGN KEY (access_form_id) REFERENCES access_form (id);

DROP TABLE ACCESS_CRITERIA CASCADE;

DROP TABLE ACCESS_CRITERIA_SECTION CASCADE;

DROP TABLE ACCESS_CRITERIA_SECTION_LINK CASCADE;

DROP TABLE ACCESS_CRITERIA_SET CASCADE;

ALTER TABLE RESOURCE
    DROP COLUMN ACCESS_CRITERIA_SET_ID;
ALTER TABLE access_criteria
    DROP CONSTRAINT access_criteria_created_by_fkey;

ALTER TABLE access_criteria
    DROP CONSTRAINT access_criteria_modified_by_fkey;

ALTER TABLE access_criteria_section
    DROP CONSTRAINT access_criteria_section_created_by_fkey;

ALTER TABLE access_criteria_section_link
    DROP CONSTRAINT access_criteria_section_link_created_by_fkey;

ALTER TABLE access_criteria_section_link
    DROP CONSTRAINT access_criteria_section_link_modified_by_fkey;

ALTER TABLE access_criteria_section
    DROP CONSTRAINT access_criteria_section_modified_by_fkey;

ALTER TABLE access_criteria_set
    DROP CONSTRAINT access_criteria_set_created_by_fkey;

ALTER TABLE access_criteria_set
    DROP CONSTRAINT access_criteria_set_modified_by_fkey;

ALTER TABLE resource
    DROP CONSTRAINT fk1xa946oabsglyyf25u09d0nuu;

ALTER TABLE access_criteria_section_link
    DROP CONSTRAINT fkcgj2extn02c91q2ld1xvp54di;

ALTER TABLE access_criteria_section_link
    DROP CONSTRAINT fkhwqk0a4nxt3p18e97l4llg4bs;

ALTER TABLE access_criteria_section
    DROP CONSTRAINT fkijwhm9gjj8qdo5eu09iibxdbo;

ALTER TABLE access_criteria
    DROP CONSTRAINT fkt4trpxmxhenaxg0i4duggiocr;

ALTER TABLE notification
    DROP CONSTRAINT notification_created_by_fkey;

ALTER TABLE notification
    DROP CONSTRAINT notification_modified_by_fkey;

CREATE SEQUENCE IF NOT EXISTS access_form_element_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS access_form_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS access_form_section_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS access_form_section_link_id_seq START WITH 100 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS section_element_link_id_seq START WITH 100 INCREMENT BY 50;

CREATE TABLE access_form
(
    id            BIGINT NOT NULL,
    creation_date TIMESTAMP WITHOUT TIME ZONE,
    modified_date TIMESTAMP WITHOUT TIME ZONE,
    created_by    BIGINT,
    modified_by   BIGINT,
    name          VARCHAR(255),
    CONSTRAINT pk_accessform PRIMARY KEY (id)
);

CREATE TABLE access_form_element
(
    id                     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    creation_date          TIMESTAMP WITHOUT TIME ZONE,
    modified_date          TIMESTAMP WITHOUT TIME ZONE,
    created_by             BIGINT,
    modified_by            BIGINT,
    name                   VARCHAR(255)                            NOT NULL,
    label                  VARCHAR(255)                            NOT NULL,
    description            VARCHAR(255)                            NOT NULL,
    type                   VARCHAR(255)                            NOT NULL,
    access_form_section_id BIGINT,
    CONSTRAINT pk_accessformelement PRIMARY KEY (id)
);

CREATE TABLE access_form_section
(
    id            BIGINT       NOT NULL,
    creation_date TIMESTAMP WITHOUT TIME ZONE,
    modified_date TIMESTAMP WITHOUT TIME ZONE,
    created_by    BIGINT,
    modified_by   BIGINT,
    name          VARCHAR(255) NOT NULL,
    label         VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_accessformsection PRIMARY KEY (id)
);

CREATE TABLE access_form_section_element_link
(
    id                          BIGINT  NOT NULL,
    access_form_section_link_id BIGINT,
    access_form_element_id      BIGINT,
    is_required                 BOOLEAN NOT NULL,
    element_order               INTEGER NOT NULL,
    CONSTRAINT pk_accessformsectionelementlink PRIMARY KEY (id)
);

CREATE TABLE access_form_section_link
(
    id                     BIGINT  NOT NULL,
    access_form_id         BIGINT,
    access_form_section_id BIGINT,
    section_order          INTEGER NOT NULL,
    CONSTRAINT pk_accessformsectionlink PRIMARY KEY (id)
);

ALTER TABLE resource
    ADD access_form_id BIGINT;

ALTER TABLE access_form_section_link
    ADD CONSTRAINT uc_accessformsectionlink_acfoidacfoseid UNIQUE (access_form_id, access_form_section_id);

ALTER TABLE access_form_element
    ADD CONSTRAINT FK_ACCESSFORMELEMENT_ON_ACCESS_FORM_SECTION FOREIGN KEY (access_form_section_id) REFERENCES access_form_section (id);

ALTER TABLE access_form_element
    ADD CONSTRAINT FK_ACCESSFORMELEMENT_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES person (id);

ALTER TABLE access_form_element
    ADD CONSTRAINT FK_ACCESSFORMELEMENT_ON_MODIFIED_BY FOREIGN KEY (modified_by) REFERENCES person (id);

ALTER TABLE access_form_section_element_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONELEMENTLINK_ON_ACCESS_FORM_ELEMENT FOREIGN KEY (access_form_element_id) REFERENCES access_form_element (id);

ALTER TABLE access_form_section_element_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONELEMENTLINK_ON_ACCESS_FORM_SECTION_LINK FOREIGN KEY (access_form_section_link_id) REFERENCES access_form_section_link (id);

ALTER TABLE access_form_section_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONLINK_ON_ACCESS_FORM FOREIGN KEY (access_form_id) REFERENCES access_form (id);

ALTER TABLE access_form_section_link
    ADD CONSTRAINT FK_ACCESSFORMSECTIONLINK_ON_ACCESS_FORM_SECTION FOREIGN KEY (access_form_section_id) REFERENCES access_form_section (id);

ALTER TABLE access_form_section
    ADD CONSTRAINT FK_ACCESSFORMSECTION_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES person (id);

ALTER TABLE access_form_section
    ADD CONSTRAINT FK_ACCESSFORMSECTION_ON_MODIFIED_BY FOREIGN KEY (modified_by) REFERENCES person (id);

ALTER TABLE access_form
    ADD CONSTRAINT FK_ACCESSFORM_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES person (id);

ALTER TABLE access_form
    ADD CONSTRAINT FK_ACCESSFORM_ON_MODIFIED_BY FOREIGN KEY (modified_by) REFERENCES person (id);

ALTER TABLE resource
    ADD CONSTRAINT FK_RESOURCE_ON_ACCESS_FORM FOREIGN KEY (access_form_id) REFERENCES access_form (id);

DROP TABLE access_criteria CASCADE;

DROP TABLE access_criteria_section CASCADE;

DROP TABLE access_criteria_section_link CASCADE;

DROP TABLE access_criteria_set CASCADE;

ALTER TABLE resource
    DROP COLUMN access_criteria_set_id;