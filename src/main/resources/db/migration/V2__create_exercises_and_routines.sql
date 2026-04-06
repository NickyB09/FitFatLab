
CREATE TABLE exercises (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(120) NOT NULL,
    muscle_group VARCHAR(80)  NOT NULL,
    equipment    VARCHAR(80),
    difficulty   VARCHAR(20)  NOT NULL CHECK (difficulty IN ('BEGINNER','INTERMEDIATE','ADVANCED')),
    description  TEXT,
    global       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE routines (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE routine_exercises (
    id          BIGSERIAL PRIMARY KEY,
    routine_id  UUID   NOT NULL REFERENCES routines(id) ON DELETE CASCADE,
    exercise_id UUID   NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    sets        INT    NOT NULL CHECK (sets > 0),
    reps        INT    NOT NULL CHECK (reps > 0),
    rest_seconds INT   NOT NULL DEFAULT 60 CHECK (rest_seconds >= 0)
);

CREATE INDEX idx_routine_user ON routines(user_id);
CREATE INDEX idx_routine_exercises_routine ON routine_exercises(routine_id);