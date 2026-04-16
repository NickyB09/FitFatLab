CREATE TABLE coach_student_links (
    id UUID PRIMARY KEY,
    coach_id UUID NOT NULL,
    student_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    allow_student_meal_edits BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    CONSTRAINT fk_coach_student_links_coach FOREIGN KEY (coach_id) REFERENCES users(id),
    CONSTRAINT fk_coach_student_links_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT chk_coach_student_link_status CHECK (status IN ('PENDING', 'ACTIVE', 'REJECTED', 'ENDED')),
    CONSTRAINT chk_coach_student_link_users CHECK (coach_id <> student_id)
);

CREATE INDEX idx_coach_student_links_coach ON coach_student_links(coach_id);
CREATE INDEX idx_coach_student_links_student ON coach_student_links(student_id);
CREATE INDEX idx_coach_student_links_status ON coach_student_links(status);
CREATE UNIQUE INDEX uq_coach_student_active_student ON coach_student_links(student_id) WHERE status = 'ACTIVE';
