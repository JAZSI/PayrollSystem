import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faLock } from '@fortawesome/free-solid-svg-icons'

export function LockedBanner({ period }: { period: string }) {
  return (
    <div className="flex items-center gap-2 rounded-md border border-amber-300 bg-amber-50 px-3 py-2 text-sm text-amber-800">
      <FontAwesomeIcon icon={faLock} />
      Cut-off <strong>{period}</strong> is locked — changes are disabled.
    </div>
  )
}
