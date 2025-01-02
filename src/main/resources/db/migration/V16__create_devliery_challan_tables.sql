drop table delivery_challan_items;
drop table delivery_challan;


CREATE TABLE delivery_challan
(
    id                        VARCHAR(255) PRIMARY KEY,
    delivery_order_id         VARCHAR(255) REFERENCES delivery_orders (id) ON DELETE CASCADE,
    date_of_challan           BIGINT,
    status                    TEXT NOT NULL,
    created_at                BIGINT NOT NULL,
    updated_at                BIGINT NOT NULL
);

CREATE TABLE delivery_challan_items
(
    id                     VARCHAR(255) PRIMARY KEY,
    delivery_challan_id    VARCHAR(255) REFERENCES delivery_challan (id) ON DELETE CASCADE,
    delivery_order_item_id VARCHAR(255) REFERENCES delivery_order_items (id) ON DELETE CASCADE,
    delivering_quantity    DOUBLE PRECISION DEFAULT 0.0
);

