CREATE TABLE IF NOT EXISTS employees (
    id                  TEXT        PRIMARY KEY,
    name                TEXT        NOT NULL,
    type                TEXT        NOT NULL,
    rate                REAL        NOT NULL,
    sick_leave          INTEGER     NOT NULL DEFAULT 0,
    vacation_leave      INTEGER     NOT NULL DEFAULT 0,
    emergency_leave     INTEGER     NOT NULL DEFAULT 0,
    loan_balance        REAL        NOT NULL DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS accounts (
    username            TEXT        PRIMARY KEY,
    password_hash       TEXT        NOT NULL,
    role                TEXT        NOT NULL,
    linked_employee_id  TEXT,
    FOREIGN KEY (linked_employee_id) REFERENCES employees(id)
);

INSERT OR IGNORE INTO accounts (username, password_hash, role, linked_employee_id)
VALUES ('admin', 'admin123', 'ADMIN', NULL);

CREATE TABLE IF NOT EXISTS attendance (
    id                  INTEGER     PRIMARY KEY AUTOINCREMENT,
    employee_id         TEXT        NOT NULL,
    record_date         TEXT        NOT NULL,
    time_in             REAL,
    time_out            REAL,
    UNIQUE (employee_id, record_date),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS submissions (
    id                  INTEGER     PRIMARY KEY AUTOINCREMENT,
    employee_id         TEXT        NOT NULL,
    leave_days          REAL        NOT NULL DEFAULT 0,
    ot_hours            REAL        NOT NULL DEFAULT 0,
    loan_deduction      REAL        NOT NULL DEFAULT 0,
    status              TEXT        NOT NULL DEFAULT 'PENDING',
    submitted_at        TEXT        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS leave_transactions (
    id                  INTEGER     PRIMARY KEY AUTOINCREMENT,
    employee_id         TEXT        NOT NULL,
    leave_type          TEXT        NOT NULL,
    days                INTEGER     NOT NULL,
    cutoff_period       TEXT        NOT NULL,
    created_at          TEXT        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS loan_transactions (
    id                  INTEGER     PRIMARY KEY AUTOINCREMENT,
    employee_id         TEXT        NOT NULL,
    amount              REAL        NOT NULL,
    cutoff_period       TEXT        NOT NULL,
    created_at          TEXT        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS payroll_entries (
    id                      INTEGER     PRIMARY KEY AUTOINCREMENT,
    employee_id             TEXT        NOT NULL,
    cutoff_period           TEXT        NOT NULL,
    total_hours             REAL        NOT NULL DEFAULT 0,
    overtime_hours          REAL        NOT NULL DEFAULT 0,
    undertime_hours         REAL        NOT NULL DEFAULT 0,
    absent_days             INTEGER     NOT NULL DEFAULT 0,
    basic_pay               REAL        NOT NULL DEFAULT 0,
    overtime_pay            REAL        NOT NULL DEFAULT 0,
    holiday_pay             REAL        NOT NULL DEFAULT 0,
    night_shift_differential REAL       NOT NULL DEFAULT 0,
    gross_pay               REAL        NOT NULL DEFAULT 0,
    sss_deduction           REAL        NOT NULL DEFAULT 0,
    philhealth_deduction    REAL        NOT NULL DEFAULT 0,
    pagibig_deduction       REAL        NOT NULL DEFAULT 0,
    tax_deduction           REAL        NOT NULL DEFAULT 0,
    loan_deduction          REAL        NOT NULL DEFAULT 0,
    undertime_penalty       REAL        NOT NULL DEFAULT 0,
    absence_penalty         REAL        NOT NULL DEFAULT 0,
    net_pay                 REAL        NOT NULL DEFAULT 0,
    created_at              TEXT        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);