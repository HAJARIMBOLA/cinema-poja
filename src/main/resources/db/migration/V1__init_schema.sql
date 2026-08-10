CREATE TABLE users (
    id          UUID PRIMARY KEY,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    birthdate   DATE NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(50) NOT NULL,
    role        VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'EMPLOYEE', 'MANAGER'))
);

CREATE TABLE rooms (
    id        UUID PRIMARY KEY,
    number    VARCHAR(50) NOT NULL UNIQUE,
    capacity  INTEGER NOT NULL CHECK (capacity > 0)
);

CREATE TABLE seats (
    id       UUID PRIMARY KEY,
    number   VARCHAR(50) NOT NULL,
    room_id  UUID NOT NULL REFERENCES rooms (id) ON DELETE CASCADE,
    UNIQUE (room_id, number)
);

CREATE TABLE movies (
    id           UUID PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    genre        VARCHAR(20) NOT NULL CHECK (
        genre IN ('THRILLER', 'ROMANCE', 'COMEDY', 'DRAMA', 'ACTION', 'SCI_FI', 'FANTASY', 'ANIMATION')
    ),
    description  VARCHAR(2000),
    duration     BIGINT NOT NULL
);

CREATE TABLE projections (
    id          UUID PRIMARY KEY,
    datetime    TIMESTAMPTZ NOT NULL,
    seat_price  NUMERIC(10, 2) NOT NULL CHECK (seat_price > 0),
    movie_id    UUID NOT NULL REFERENCES movies (id) ON DELETE RESTRICT,
    room_id     UUID NOT NULL REFERENCES rooms (id) ON DELETE RESTRICT
);

CREATE INDEX idx_projections_room_datetime ON projections (room_id, datetime);

CREATE TABLE reservations (
    id            UUID PRIMARY KEY,
    created_at    TIMESTAMPTZ NOT NULL,
    user_id       UUID NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    projection_id UUID NOT NULL REFERENCES projections (id) ON DELETE RESTRICT
);

CREATE INDEX idx_reservations_user ON reservations (user_id);

CREATE TABLE reservation_seat (
    reservation_id  UUID NOT NULL REFERENCES reservations (id) ON DELETE CASCADE,
    seat_id         UUID NOT NULL REFERENCES seats (id) ON DELETE RESTRICT,
    PRIMARY KEY (reservation_id, seat_id)
);
