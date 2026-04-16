CREATE TABLE routine_templates (
    id UUID PRIMARY KEY,
    coach_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    template_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routine_templates_coach FOREIGN KEY (coach_id) REFERENCES users(id),
    CONSTRAINT chk_routine_template_type CHECK (template_type IN ('GENERIC', 'CUSTOM'))
);

CREATE TABLE routine_template_exercises (
    id BIGSERIAL PRIMARY KEY,
    routine_template_id UUID NOT NULL,
    exercise_id UUID NOT NULL,
    sets INT NOT NULL,
    reps INT NOT NULL,
    rest_seconds INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_routine_template_exercises_template FOREIGN KEY (routine_template_id) REFERENCES routine_templates(id) ON DELETE CASCADE,
    CONSTRAINT fk_routine_template_exercises_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id)
);

CREATE TABLE assigned_routines (
    id UUID PRIMARY KEY,
    coach_id UUID NOT NULL,
    student_id UUID NOT NULL,
    template_id UUID NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    period_type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    scheduled_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_assigned_routines_coach FOREIGN KEY (coach_id) REFERENCES users(id),
    CONSTRAINT fk_assigned_routines_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT fk_assigned_routines_template FOREIGN KEY (template_id) REFERENCES routine_templates(id),
    CONSTRAINT chk_assigned_routine_period_type CHECK (period_type IN ('DAY', 'WEEK')),
    CONSTRAINT chk_assigned_routine_status CHECK (status IN ('ASSIGNED', 'COMPLETED'))
);

CREATE TABLE assigned_routine_exercises (
    id BIGSERIAL PRIMARY KEY,
    assigned_routine_id UUID NOT NULL,
    exercise_id UUID NOT NULL,
    sets INT NOT NULL,
    reps INT NOT NULL,
    rest_seconds INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_assigned_routine_exercises_assignment FOREIGN KEY (assigned_routine_id) REFERENCES assigned_routines(id) ON DELETE CASCADE,
    CONSTRAINT fk_assigned_routine_exercises_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id)
);

CREATE INDEX idx_routine_templates_coach ON routine_templates(coach_id);
CREATE INDEX idx_assigned_routines_student_date ON assigned_routines(student_id, scheduled_date DESC);
CREATE INDEX idx_assigned_routines_coach_date ON assigned_routines(coach_id, scheduled_date DESC);
