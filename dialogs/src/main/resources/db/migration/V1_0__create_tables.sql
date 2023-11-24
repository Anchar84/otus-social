create table if not exists t_dialogs
(
    id        serial,
    fromUserId integer not null,
    shadr_key text not null,
    toUserId integer not null,
    createdAt timestamp not null default now(),
    text   text not null
);

SELECT create_distributed_table('t_dialogs', 'shadr_key');
