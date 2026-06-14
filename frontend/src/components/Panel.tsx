import type { ReactNode } from 'react'
import { cn } from '@/lib/utils'

/** Sharp, compact dashboard panel with a technical label row. */
export function Panel({
  title,
  description,
  action,
  className,
  bodyClassName,
  children,
}: {
  title?: string
  description?: string
  action?: ReactNode
  className?: string
  bodyClassName?: string
  children: ReactNode
}) {
  return (
    <div
      className={cn(
        'bg-card ring-border/70 flex flex-col rounded-lg ring-1',
        className,
      )}
    >
      {(title || action) && (
        <div className="flex items-center justify-between gap-3 border-b border-border/60 px-4 py-2.5">
          <div className="min-w-0">
            {title && (
              <div className="text-muted-foreground text-[11px] font-semibold tracking-wider uppercase">
                {title}
              </div>
            )}
            {description && <div className="text-foreground/80 truncate text-xs">{description}</div>}
          </div>
          {action}
        </div>
      )}
      <div className={cn('p-4', bodyClassName)}>{children}</div>
    </div>
  )
}
