create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    full_name varchar(255) not null,
    password_hash varchar(255) not null,
    created_at timestamp with time zone not null default now(),
    role varchar(255) not null default 'USER'
);


create table organizations (
    id bigserial primary key,
    name varchar(255) not null,
    description varchar(500),
    created_at timestamp with time zone not null default now(),
    owner_id bigint not null,

    constraint fk_organization_owner
        foreign key (owner_id)
        references users(id)
        on delete cascade
);
