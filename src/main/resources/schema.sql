-- Пользователи
CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL,
CONSTRAINT pk_user PRIMARY KEY (id),
CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

-- Вещи
CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
description VARCHAR(512) NOT NULL,
is_available BOOLEAN NOT NULL,
user_id BIGINT,
CONSTRAINT pk_item PRIMARY KEY (id),
CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Бронирование
CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
item_id BIGINT,
user_id BIGINT,
status VARCHAR,
CONSTRAINT pk_booking PRIMARY KEY (id),
CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
CONSTRAINT fk_bookings_to_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Комментарии
CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
text VARCHAR(512) NOT NULL,
item_id BIGINT,
user_id BIGINT,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT pk_comment PRIMARY KEY (id),
CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
CONSTRAINT fk_comments_to_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
)