
CREATE TABLE progress_records (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    weight_kg     DECIMAL(5,2),
    body_fat_pct  DECIMAL(4,2),
    record_date   DATE         NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT uq_progress_user_date UNIQUE (user_id, record_date)
);

CREATE INDEX idx_progress_user_date ON progress_records(user_id, record_date);