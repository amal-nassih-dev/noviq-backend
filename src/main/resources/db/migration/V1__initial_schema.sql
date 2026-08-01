create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    full_name varchar(255) not null,
    password_hash varchar(255) not null,
    created_at timestamp with time zone not null default now(),
    role varchar(255) not null default 'USER'
);
