create table employee (
    id varchar(255) primary key,
    name varchar(255) not null,
    email varchar(255) not null unique,
    contact_number varchar(255) not null,
    role varchar(255) not null,
    created_at bigint
);
