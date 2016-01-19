--liquibase formatted sql

--changeset archived_card:2
ALTER TABLE archived_card ADD COLUMN user_id text;
--rollback alter table drop column user_id
