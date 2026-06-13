import { useEffect, useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faGear } from '@fortawesome/free-solid-svg-icons'
import { toast } from 'sonner'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { useSettings, useUpdateSettings } from '@/hooks/usePayroll'
import { toApiError } from '@/api/client'
import type { Settings } from '@/types/payroll'

const FIELDS: { key: keyof Settings; label: string; hint?: string }[] = [
  { key: 'workingDays', label: 'Working days / month' },
  { key: 'workdayStartHour', label: 'Workday start (hour)', hint: 'e.g. 8 = 8:00 AM' },
  { key: 'overtimeStartHour', label: 'Overtime start (hour)', hint: 'e.g. 17 = 5:00 PM' },
  { key: 'lunchStartHour', label: 'Lunch start (hour)', hint: 'e.g. 11 = 11:00 AM' },
  { key: 'leaveRegular', label: 'Leave credits — Regular' },
  { key: 'leaveProbationary', label: 'Leave credits — Probationary' },
  { key: 'leaveContractual', label: 'Leave credits — Contractual' },
  { key: 'leavePartTimer', label: 'Leave credits — Part-Timer' },
]

export function SettingsPage() {
  const { data, isLoading } = useSettings()
  const update = useUpdateSettings()
  const [form, setForm] = useState<Settings | null>(null)

  useEffect(() => {
    if (data) setForm(data)
  }, [data])

  function save() {
    if (!form) return
    update.mutate(form, {
      onSuccess: () => toast.success('Settings saved'),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <FontAwesomeIcon icon={faGear} className="text-muted-foreground" />
          Payroll Settings
        </CardTitle>
        <CardDescription>
          Schedule and leave-credit policy used by every payroll run.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {isLoading || !form ? (
          <p className="text-muted-foreground text-sm">Loading…</p>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {FIELDS.map((f) => (
              <div key={f.key} className="grid gap-1.5">
                <Label>{f.label}</Label>
                <Input
                  type="number"
                  value={form[f.key]}
                  onChange={(e) =>
                    setForm({ ...form, [f.key]: Number(e.target.value) })
                  }
                />
                {f.hint && (
                  <p className="text-muted-foreground text-xs">{f.hint}</p>
                )}
              </div>
            ))}
            <div className="sm:col-span-2">
              <Button onClick={save} disabled={update.isPending}>
                {update.isPending ? 'Saving…' : 'Save settings'}
              </Button>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
