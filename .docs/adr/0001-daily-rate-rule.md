# ADR 0001 — Single daily-rate rule

- **Status:** Accepted
- **Date:** 2026-06-12
- **Context doc:** computation rules in [formulas.md](../formulas.md); overview in [00-system-overview.md](../00-system-overview.md)

## Context

The codebase defined the employee daily rate in **two** places that disagreed
whenever `workingDaysPerMonth ≠ 26`:

1. `Employee.computeDailyRate()` (abstract) → overridden in each subclass as
   `monthlyRate / 26.0` (or `hourlyRate * 8.0` for part-timers). **Never called.**
2. `PayrollCalculator.computeDailyRate(employee, settings)` (private) →
   `monthlyRate / settings.getWorkingDaysPerMonth()`. **The one actually used** for
   overtime hourly rate, absence penalty, and undertime penalty.

## Decision

Adopt **(2)** — the settings-driven `monthlyRate / workingDaysPerMonth` — as the single
canonical daily-rate rule. Remove the unused abstract `Employee.computeDailyRate()` and
its four subclass overrides.

## Rationale

- (2) is configurable (honors the user-entered "working days in a month"); (1) hard-codes
  26 and is dead code.
- Removing (1) is **behavior-preserving** — it was never invoked, confirmed by grep. All
  10 characterization tests remained green after removal.

## Consequences

- `Employee` no longer declares an abstract method but remains `abstract` (a bare
  `Employee` is still not instantiable, which is intended).
- Part-timer daily rate (`hourlyRate * 8`) is no longer represented anywhere; it was
  unused. Part-timer pay is hours-based and does not need a daily rate today. If a future
  feature needs it, reintroduce it deliberately on the polymorphic `Employee`.
