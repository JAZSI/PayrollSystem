import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faClockRotateLeft, faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { useAudit } from '@/hooks/useAudit'
import { AUDIT_ENTITIES } from '@/types/audit'

const ALL = 'ALL'

export function AuditLogPage() {
  const [entity, setEntity] = useState<string>(ALL)
  const [page, setPage] = useState(0)
  const { data } = useAudit(entity === ALL ? undefined : entity, page)

  function changeEntity(value: string) {
    setEntity(value)
    setPage(0)
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-start justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faClockRotateLeft} className="text-muted-foreground" />
            Audit Log
          </CardTitle>
          <CardDescription>Immutable trail of who did what, newest first.</CardDescription>
        </div>
        <Select value={entity} onValueChange={(v) => changeEntity(v ?? ALL)}>
          <SelectTrigger className="w-44">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL}>All entities</SelectItem>
            {AUDIT_ENTITIES.map((e) => (
              <SelectItem key={e} value={e}>
                {e}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>When</TableHead>
              <TableHead>User</TableHead>
              <TableHead>Action</TableHead>
              <TableHead>Entity</TableHead>
              <TableHead>Summary</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data && data.content.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} className="text-muted-foreground text-center">
                  No audit entries.
                </TableCell>
              </TableRow>
            )}
            {data?.content.map((a) => (
              <TableRow key={a.id}>
                <TableCell className="text-muted-foreground text-xs whitespace-nowrap">
                  {a.createdAt ? a.createdAt.replace('T', ' ').slice(0, 19) : ''}
                </TableCell>
                <TableCell>
                  {a.username}
                  {a.role && <span className="text-muted-foreground ml-1 text-xs">({a.role})</span>}
                </TableCell>
                <TableCell>
                  <Badge variant="secondary">{a.action}</Badge>
                </TableCell>
                <TableCell>
                  {a.entity}
                  {a.entityId && <span className="text-muted-foreground ml-1 font-mono text-xs">#{a.entityId}</span>}
                </TableCell>
                <TableCell className="text-sm">{a.summary}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        {data && data.totalPages > 1 && (
          <div className="mt-4 flex items-center justify-end gap-3 text-sm">
            <span className="text-muted-foreground">
              Page {data.page + 1} of {data.totalPages}
            </span>
            <Button
              variant="outline"
              size="sm"
              disabled={data.page <= 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              <FontAwesomeIcon icon={faChevronLeft} />
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={data.page >= data.totalPages - 1}
              onClick={() => setPage((p) => p + 1)}
            >
              <FontAwesomeIcon icon={faChevronRight} />
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
