CREATE TABLE category
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- BIGINT REFERENCES table(column): declares this column as a foreign key.
CREATE TABLE product
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    description    TEXT,
    price          NUMERIC(10, 2) NOT NULL,
    stock_quantity INTEGER        NOT NULL,
    category_id    BIGINT REFERENCES category (id),
    image_url      VARCHAR(500),
    created_at     TIMESTAMP      NOT NULL
);

CREATE TABLE app_user
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL
);

CREATE TABLE cart
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES app_user (id) UNIQUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE cart_item
(
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT REFERENCES cart (id),
    product_id BIGINT REFERENCES product (id),
    quantity   INTEGER NOT NULL,
    UNIQUE (cart_id, product_id)
);

CREATE TABLE app_order
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES app_user (id),
    status     VARCHAR(255)   NOT NULL,
    created_at TIMESTAMP      NOT NULL,
    total      NUMERIC(10, 2) NOT NULL
);

CREATE TABLE order_item
(
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT REFERENCES app_order (id),
    product_id        BIGINT REFERENCES product (id),
    quantity          INTEGER        NOT NULL,
    price_at_purchase NUMERIC(10, 2) NOT NULL
);
