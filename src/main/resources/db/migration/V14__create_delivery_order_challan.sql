CREATE TABLE delivery_challan
(
    id                        VARCHAR(255) PRIMARY KEY,
    delivery_order_id         VARCHAR(255) REFERENCES delivery_orders (id) ON DELETE CASCADE,
    date_of_challan           BIGINT,
    status                    VARCHAR,
    created_at                BIGINT,
    updated_at                BIGINT
);

CREATE TABLE delivery_challan_item
(
    id                     VARCHAR(255) PRIMARY KEY,
    delivery_challan_id    VARCHAR(255) REFERENCES delivery_challan (id) ON DELETE CASCADE,
    delivery_order_item_id VARCHAR(255) REFERENCES delivery_order_items (id) ON DELETE CASCADE,
    delivering_quantity    DOUBLE PRECISION DEFAULT 0.0
);



