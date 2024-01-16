create table if not exists t_counter
(
    id        serial primary key,
    entityId   text not null,
    entityType text not null,
    userId  integer,
    constraint uk_counter unique (entityId, entityType)
);

create table if not exists t_counter_sum
(
    id        serial primary key,
    userId  integer not null,
    entityType text not null,
    sum integer,
    constraint uk_counter_sum unique (userId, entityType)
);
