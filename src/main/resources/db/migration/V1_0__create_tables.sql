create table if not exists t_user
(
    id        uuid DEFAULT gen_random_uuid() primary key,
    first_name      varchar not null,
    second_name    varchar,
    age       numeric,
    birthdate date,
    biography varchar,
    city      varchar
);

create table if not exists t_user_credentials
(
    user_id uuid primary key references t_user(id),
    passwd_hash varchar,
    passwd_salt varchar
)

