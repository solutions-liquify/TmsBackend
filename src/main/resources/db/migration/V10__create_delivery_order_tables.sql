-- Table for DeliveryOrder
CREATE TABLE delivery_order (
    id UUID PRIMARY KEY,
    contract_id TEXT NOT NULL,
    party_id TEXT NOT NULL,
    date_of_contract BIGINT,
    status TEXT NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Table for DeliveryOrderItem
CREATE TABLE delivery_order_item (
    id UUID PRIMARY KEY,
    delivery_order_id UUID REFERENCES delivery_order(id) ON DELETE CASCADE,
    district TEXT NOT NULL,
    taluka TEXT NOT NULL,
    location TEXT NOT NULL,
    material_id TEXT,
    quantity DOUBLE PRECISION DEFAULT 0.0,
    rate DOUBLE PRECISION DEFAULT 0.0,
    due_date BIGINT,
    status TEXT NOT NULL
);