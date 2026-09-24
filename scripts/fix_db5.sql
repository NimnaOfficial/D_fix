
PRAGMA foreign_keys=off;
DROP TRIGGER IF EXISTS update_branch_spare_parts_updated_at;

-- Fix messages table CASCADE
CREATE TABLE messages_new (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    sender_id TEXT NOT NULL,
    receiver_id TEXT NOT NULL,
    message TEXT,
    image_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY(sender_id) REFERENCES users(id),
    FOREIGN KEY(receiver_id) REFERENCES users(id)
);
INSERT INTO messages_new SELECT * FROM messages;
DROP TABLE messages;
ALTER TABLE messages_new RENAME TO messages;

-- Fix appointments table CHECK constraint
CREATE TABLE appointments_new (
    id TEXT PRIMARY KEY,
    appointment_number TEXT NOT NULL UNIQUE,
    customer_id TEXT NOT NULL,
    device_id TEXT NOT NULL,
    service_id TEXT NOT NULL,
    branch_id TEXT,
    technician_id TEXT,
    requested_date TEXT NOT NULL,
    requested_time TEXT,
    customer_latitude REAL,
    customer_longitude REAL,
    problem_description TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'REQUESTED'
        CHECK (status IN (
            'REQUESTED',
            'CONFIRMED',
            'ASSIGNED',
            'DEVICE_RECEIVED',
            'DIAGNOSING',
            'REPAIRING',
            'TESTING',
            'READY',
            'COMPLETED',
            'CANCELLED',
            'SUSPENDED'
        )),
    estimated_price REAL,
    final_price REAL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (device_id) REFERENCES devices(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (technician_id) REFERENCES technicians(id)
);
INSERT INTO appointments_new SELECT * FROM appointments;
DROP TABLE appointments;
ALTER TABLE appointments_new RENAME TO appointments;

PRAGMA foreign_keys=on;

