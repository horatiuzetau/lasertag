--liquibase formatted sql

--changeset horatiu.atanasoaei:01
--comment Initialize database for reservation application
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create type time_range as
(
    start_time time,
    end_time   time
);

create table settings
(
    name  varchar(255) not null unique,
    value varchar(255) not null
);

create table clients
(
    id                       serial primary key,
    created_at               timestamp not null default current_timestamp,
    updated_at               timestamp not null default current_timestamp,
    first_name               varchar(500),
    last_name                varchar(500),
    email                    varchar(500)
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    phone                    varchar(50),
    subscribed_to_newsletter boolean            default false
);

create table activities
(
    id            serial primary key,
    name          varchar(100)     not null,
    duration      int              not null check (duration >= 0),
    recovery_time int              not null check (recovery_time >= 0),
    capacity      int              not null check (capacity > 0),
    price         double precision not null check (price > 0),
    type          varchar          not null check (type IN ('SINGLE', 'BUNDLE', 'ADDON')),
    shareable     boolean          not null,
    active        boolean          not null,
    deleted       boolean          not null,
    bundle_id     int,
    constraint fk_activities_bundle_to_activities foreign key (bundle_id) references activities (id)
);

create table activities_in_bundle
(
    bundle_id   int,
    activity_id int,
    size        int not null check (size > 0),
    primary key (bundle_id, activity_id)
);

create table schedules
(
    id          serial primary key,
    name        varchar(100) not null,
    description varchar(1000),
    created_at  timestamp    not null default current_timestamp,
    updated_at  timestamp    not null default current_timestamp,
    main        boolean      not null default false,
    active      boolean      not null default false,
    monday      time_range[],
    tuesday     time_range[],
    wednesday   time_range[],
    thursday    time_range[],
    friday      time_range[],
    saturday    time_range[],
    sunday      time_range[],
    start_date  date         not null,
    end_date    date         not null,
    deleted     boolean               default false,
    -- Check constraint to ensure that active is true if main is true
    CONSTRAINT check_active_if_main
        CHECK ((main = false) OR (active = true)),
    -- Check constraint to ensure that start_date is before end_date
    CONSTRAINT check_start_before_end
        CHECK ((start_date IS NULL AND end_date IS NULL) OR (start_date < end_date))
);

-- Create a partial unique index to ensure only one main schedule exists
CREATE UNIQUE INDEX unique_main_schedule ON schedules (main)
    WHERE main = true;

create table slots
(
    id           serial primary key,
    created_at   timestamp not null default current_timestamp,
    updated_at   timestamp not null default current_timestamp,
    "date"       date      not null,
    start_time   time      not null,
    end_time     time      not null,
    status       varchar   not null check (status IN ('BOOKED', 'CANCELLED', 'CONFIRMED', 'BLOCKED')),
    booked_spots int       not null check (booked_spots >= 0),
    client_id    int,
    schedule_id  int,
    activity_id  int,
    bundle_id    int,
    constraint fk_slots_to_clients foreign key (client_id) references clients (id),
    constraint fk_slots_to_schedules foreign key (schedule_id) references schedules (id),
    constraint fk_slots_to_services foreign key (activity_id) references activities (id),
    constraint fk_slots_to_bundle_slots foreign key (bundle_id) references slots (id)
);

create table roles
(
    id   serial primary key,
    name varchar(50) not null unique
);

create table users
(
    id                      serial primary key,
    username                varchar(50)  not null unique,
    password                varchar(255) not null,
    role_id                 int,
    constraint fk_users_to_roles foreign key (role_id) references roles (id)
);

-- Insert values
INSERT INTO settings(name, value)
VALUES ('bookings.enabled', 'true');

-- Roles
INSERT INTO roles(name)
VALUES ('ADMIN');

-- User
INSERT INTO users(username, password, role_id)
VALUES ('admin', '$2a$12$H/P.7s0UGtMubnBZ146QsuAe76.hmoOSnnwkviIYczloDym/seuc2', 1);