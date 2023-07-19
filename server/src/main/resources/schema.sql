DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(512) NOT NULL,
                       CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE requests (
                          id BIGSERIAL PRIMARY KEY,
                          description VARCHAR(512) NOT NULL,
                          requester_id BIGINT NOT NULL,
                          created TIMESTAMP WITH TIME ZONE NOT NULL,
                          CONSTRAINT fk_requests_to_users FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE items (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(512) NOT NULL,
                       is_available BOOLEAN DEFAULT NULL,
                       owner_id BIGINT NOT NULL,
                       request_id BIGINT,
                       CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
                       CONSTRAINT fk_items_to_requests FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,
                          start_date TIMESTAMP WITH TIME ZONE NOT NULL,
                          end_date TIMESTAMP WITH TIME ZONE NOT NULL,
                          item_id BIGINT NOT NULL,
                          booker_id BIGINT NOT NULL,
                          status VARCHAR(25),
                          CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
                          CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE,
                          CONSTRAINT check_bookings_status CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          text VARCHAR(512) NOT NULL,
                          item_id BIGINT NOT NULL,
                          author_id BIGINT NOT NULL,
                          created TIMESTAMP WITH TIME ZONE NOT NULL,
                          CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);