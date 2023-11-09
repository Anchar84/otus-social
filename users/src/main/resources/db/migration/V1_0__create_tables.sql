create table if not exists t_user
(
    id        serial primary key,
    first_name      varchar not null,
    second_name    varchar,
    age       numeric,
    birthdate date,
    biography varchar,
    city      varchar
);

create table if not exists t_user_credentials
(
    user_id integer primary key references t_user(id),
    passwd_hash varchar,
    passwd_salt varchar
)

