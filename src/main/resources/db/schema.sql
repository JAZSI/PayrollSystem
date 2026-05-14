CREATE TABLE IF NOT EXISTS employees (
	id                  TEXT       PRIMARY KEY,
        name                TEXT       NOT NULL,
        type                TEXT       NOT NULL,
        rate                REAL       NOT NULL,
        sick_leave          INTEGER    NOT NULL DEFAULT 0,
        vacation_leave      INTEGER    NOT NULL DEFAULT 0,
        emergency_leave     INTEGER    NOT NULL DEFAULT 0,
        loan_balance        REAL       NOT NULL DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS payroll_entries (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	employee_id TEXT NOT NULL,
	cutoff_period TEXT NOT NULL,
	gross_pay REAL NOT NULL,
	deductions REAL NOT NULL,
	net_pay REAL NOT NULL,
	created_at TEXT DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS time_records (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	employee_id TEXT NOT NULL,
	day_number INTEGER NOT NULL,
	time_in INTEGER,
	time_out INTEGER,
	absent INTEGER NOT NULL,
	holiday_type TEXT NOT NULL,
	FOREIGN KEY (employee_id) REFERENCES employees(id)
);
