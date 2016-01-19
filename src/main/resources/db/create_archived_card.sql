--liquibase formatted sql


--changeset archived_card:1
CREATE TABLE archived_card
(
  cardtext text NOT NULL,
  id bigserial NOT NULL,
  archiveddate timestamp with time zone NOT NULL,
  CONSTRAINT archived_card_pkey PRIMARY KEY (id)
)
--rollback drop table test_table_for_mike
