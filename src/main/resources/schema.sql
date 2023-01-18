CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
create table if not exists planets
(
    id          uuid not null default uuid_generate_v4(),
    name        text not null,
    climate     text not null,
    terrain     text not null,
    external_id int  not null,
    primary key (id),
    unique (external_id)
);

create table if not exists films
(
    id           uuid not null default uuid_generate_v4(),
    title        text not null,
    director     text not null,
    release_date date not null,
    external_id  int  not null,
    primary key (id),
    unique (external_id)
);

create table if not exists films_planets
(
    id        uuid not null default uuid_generate_v4(),
    film_id   uuid not null,
    planet_id uuid not null,
    primary key (id),
    foreign key (film_id) references films (id),
    foreign key (planet_id) references planets (id),
    unique (film_id, planet_id)
);