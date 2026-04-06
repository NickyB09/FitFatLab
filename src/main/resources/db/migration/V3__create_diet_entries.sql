

CREATE TABLE diet_entries (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    food_name  VARCHAR(200) NOT NULL,
    calories   INT          NOT NULL CHECK (calories >= 0),
    protein_g  DECIMAL(6,2) NOT NULL DEFAULT 0,
    carbs_g    DECIMAL(6,2) NOT NULL DEFAULT 0,
    fat_g      DECIMAL(6,2) NOT NULL DEFAULT 0,
    entry_date DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_diet_user_date ON diet_entries(user_id, entry_date);