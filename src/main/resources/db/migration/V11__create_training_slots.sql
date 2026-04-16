CREATE TABLE training_slots (
    id UUID PRIMARY KEY,
    coach_id UUID NOT NULL,
    student_id UUID NOT NULL,
    weekday VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    note VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_training_slots_coach FOREIGN KEY (coach_id) REFERENCES users(id),
    CONSTRAINT fk_training_slots_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT chk_training_slots_weekday CHECK (weekday IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'))
);

CREATE INDEX idx_training_slots_coach_weekday_time ON training_slots(coach_id, weekday, start_time);
CREATE INDEX idx_training_slots_student_weekday_time ON training_slots(student_id, weekday, start_time);
