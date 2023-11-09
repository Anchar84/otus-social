create table if not exists t_posts
(
    id        serial primary key,
    authorId  integer not null,
    authorName text not null,
    createdAt timestamp not null default now(),
    text   text not null
);

create table if not exists t_friends
(
    userId  integer not null,
    friendId integer not null,
    constraint pk_posts_feed primary key (userId, friendId)
)
