const pesoFmt = new Intl.NumberFormat('en-PH', {
  style: 'currency',
  currency: 'PHP',
})

export const peso = (n: number) => pesoFmt.format(n)

export const hours = (n: number) =>
  `${n.toLocaleString('en-PH', { maximumFractionDigits: 2 })} hr`

/** "0800" / "1700" display for an HHMM integer. */
export const hhmm = (n: number) => String(n).padStart(4, '0')

export const PERIODS = ['1st-15th', '16th-30th'] as const
export type Period = (typeof PERIODS)[number]
