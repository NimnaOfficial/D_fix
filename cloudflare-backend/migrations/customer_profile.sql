CREATE TABLE IF NOT EXISTS customers (
    user_id TEXT PRIMARY KEY,
    city TEXT,
    address TEXT,
    loyalty_points INTEGER DEFAULT 0,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO customers (user_id)
SELECT id FROM users WHERE role = 'CUSTOMER'
ON CONFLICT(user_id) DO NOTHING;
