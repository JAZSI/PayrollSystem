import { useEffect, useRef, useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faClock,
  faRightToBracket,
  faRightFromBracket,
  faCircleCheck,
  faCircleXmark,
} from '@fortawesome/free-solid-svg-icons'
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { kioskClock } from '@/api/kiosk'
import { toApiError } from '@/api/client'
import { cn } from '@/lib/utils'
import type { KioskResult } from '@/types/payroll'

type Feedback =
  | { kind: 'success'; result: KioskResult }
  | { kind: 'error'; message: string }

const actionStyles: Record<
  KioskResult['action'],
  { ring: string; icon: IconDefinition; label: string }
> = {
  CLOCK_IN: { ring: 'border-emerald-400 bg-emerald-50', icon: faRightToBracket, label: 'Clocked In' },
  CLOCK_OUT: { ring: 'border-sky-400 bg-sky-50', icon: faRightFromBracket, label: 'Clocked Out' },
  ALREADY_COMPLETE: { ring: 'border-amber-400 bg-amber-50', icon: faCircleCheck, label: 'Already Done' },
}

export function KioskPage() {
  const navigate = useNavigate()
  const [id, setId] = useState('')
  const [busy, setBusy] = useState(false)
  const [feedback, setFeedback] = useState<Feedback | null>(null)
  const [now, setNow] = useState(new Date())
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null)

  useEffect(() => {
    const t = setInterval(() => setNow(new Date()), 1000)
    return () => clearInterval(t)
  }, [])

  async function submit(e: FormEvent) {
    e.preventDefault()
    const employeeId = id.trim()
    if (!employeeId) return
    setBusy(true)
    setFeedback(null)
    try {
      const result = await kioskClock(employeeId)
      setFeedback({ kind: 'success', result })
    } catch (err) {
      setFeedback({ kind: 'error', message: toApiError(err).message })
    } finally {
      setBusy(false)
      setId('')
      if (timer.current) clearTimeout(timer.current)
      timer.current = setTimeout(() => setFeedback(null), 5000)
    }
  }

  const time = now.toLocaleTimeString('en-PH', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  const date = now.toLocaleDateString('en-PH', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  return (
    <div className="bg-background text-foreground flex min-h-screen flex-col items-center justify-center p-6">
      <div className="w-full max-w-md text-center">
        <div className="text-muted-foreground mb-1 flex items-center justify-center gap-2 text-sm">
          <FontAwesomeIcon icon={faClock} />
          PayrollPal Time Clock
        </div>
        <div className="text-6xl font-bold tabular-nums">{time}</div>
        <div className="text-muted-foreground mb-8 text-sm">{date}</div>

        <form onSubmit={submit} className="space-y-3">
          <Input
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="Enter your Employee ID"
            className="h-14 text-center text-xl"
            autoFocus
            inputMode="numeric"
          />
          <Button type="submit" disabled={busy} className="h-14 w-full text-lg">
            {busy ? 'Please wait…' : 'Clock In / Out'}
          </Button>
        </form>

        <div className="mt-6 min-h-28">
          {feedback?.kind === 'success' && (
            <div className={cn('rounded-xl border-2 p-5 text-left', actionStyles[feedback.result.action].ring)}>
              <div className="flex items-center gap-2 font-semibold text-emerald-900">
                <FontAwesomeIcon icon={actionStyles[feedback.result.action].icon} />
                {actionStyles[feedback.result.action].label} · {feedback.result.time}
              </div>
              <div className="mt-1 text-sm text-emerald-900/80">
                {feedback.result.message}
              </div>
            </div>
          )}
          {feedback?.kind === 'error' && (
            <div className="rounded-xl border-2 border-red-400 bg-red-50 p-5 text-left">
              <div className="flex items-center gap-2 font-semibold text-red-900">
                <FontAwesomeIcon icon={faCircleXmark} />
                Not recognized
              </div>
              <div className="mt-1 text-sm text-red-900/80">{feedback.message}</div>
            </div>
          )}
        </div>

        <button
          onClick={() => navigate('/login')}
          className="text-muted-foreground hover:text-foreground mt-8 text-xs underline"
        >
          Staff sign in
        </button>
      </div>
    </div>
  )
}
