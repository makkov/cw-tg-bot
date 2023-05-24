-- — liquibase formatted sql
-- changeset makkov:1

create table notification
(
    id        bigint not null
        primary key,
    chat_id   bigint not null,
    date_time timestamp,
    task      varchar(255)
);
