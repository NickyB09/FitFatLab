CREATE TABLE meal_plans (
    id UUID PRIMARY KEY,
    coach_id UUID NOT NULL,
    student_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    period_type VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    allow_student_edits BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meal_plans_coach FOREIGN KEY (coach_id) REFERENCES users(id),
    CONSTRAINT fk_meal_plans_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT chk_meal_plan_period_type CHECK (period_type IN ('DAY', 'WEEK')),
    CONSTRAINT chk_meal_plan_dates CHECK (end_date >= start_date)
);

CREATE TABLE meal_plan_meals (
    id UUID PRIMARY KEY,
    meal_plan_id UUID NOT NULL,
    meal_name VARCHAR(120) NOT NULL,
    planned_date DATE NOT NULL,
    calories INT NOT NULL,
    protein_g REAL NOT NULL,
    carbs_g REAL NOT NULL,
    fat_g REAL NOT NULL,
    CONSTRAINT fk_meal_plan_meals_plan FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id) ON DELETE CASCADE
);

CREATE INDEX idx_meal_plans_student_start ON meal_plans(student_id, start_date DESC);
CREATE INDEX idx_meal_plans_coach_start ON meal_plans(coach_id, start_date DESC);
CREATE INDEX idx_meal_plan_meals_plan_date ON meal_plan_meals(meal_plan_id, planned_date);
