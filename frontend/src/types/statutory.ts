export type ContributionAgency = 'SSS' | 'PHILHEALTH' | 'PAGIBIG' | 'BIR'

export const AGENCIES: ContributionAgency[] = ['SSS', 'PHILHEALTH', 'PAGIBIG', 'BIR']

/** Per-agency hint on what the bracket columns mean. */
export const AGENCY_HINT: Record<ContributionAgency, string> = {
  SSS: 'Salary [lower, upper) → amount = employee contribution (employer = 2×, EC added).',
  PHILHEALTH: 'Single row: lower = floor, upper = ceiling, rate (e.g. 0.055).',
  PAGIBIG: 'Salary [lower, upper) → rate (e.g. 0.01/0.02), amount = cap.',
  BIR: 'Annual [lower, upper) → amount = base tax at lower, rate = marginal rate.',
}

export interface ContributionBracket {
  id?: number
  seq: number
  lowerBound: number
  upperBound: number
  amount: number
  rate: number
}

export interface ContributionTable {
  id: number
  agency: ContributionAgency
  effectiveFrom: string
  active: boolean
  note: string | null
  brackets: ContributionBracket[]
}

export interface ContributionTableRequest {
  agency: ContributionAgency
  effectiveFrom: string
  active: boolean
  note?: string | null
  brackets: ContributionBracket[]
}
