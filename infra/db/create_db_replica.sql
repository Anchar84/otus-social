create database social_users;
create database social_posts;
create database social_counter;

--CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'replicator_password';
CREATE USER replicator with login replication password 'replicator_password';
-- SELECT pg_create_physical_replication_slot('replication_slot');
