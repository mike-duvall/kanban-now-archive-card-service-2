--liquibase formatted sql

--changeset archived_card:3
ALTER TABLE archived_card ADD COLUMN board_id bigint;

update archived_card set board_id = 6;

