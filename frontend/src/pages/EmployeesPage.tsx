import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faBan,
  faUsers,
  faPen,
  faUserPlus,
  faMoneyCheckDollar,
  faSackDollar,
} from '@fortawesome/free-solid-svg-icons'
import { toast } from 'sonner'
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
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { EmployeeFormDialog } from '@/components/EmployeeFormDialog'
import { LoansDialog } from '@/components/LoansDialog'
import { PayItemsDialog } from '@/components/PayItemsDialog'
import { useDeactivateEmployee, useEmployees } from '@/hooks/useEmployees'
import { toApiError } from '@/api/client'
import type { Employee } from '@/types/employee'

const peso = new Intl.NumberFormat('en-PH', {
  style: 'currency',
  currency: 'PHP',
})

function rateOf(e: Employee): string {
  return e.type === 'PART_TIMER'
    ? `${peso.format(e.hourlyRate)} / hr`
    : `${peso.format(e.monthlyRate)} / mo`
}

export function EmployeesPage() {
  const { data: employees, isLoading, isError } = useEmployees()
  const deactivate = useDeactivateEmployee()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Employee | null>(null)
  const [loansFor, setLoansFor] = useState<Employee | null>(null)
  const [payItemsFor, setPayItemsFor] = useState<Employee | null>(null)

  function openAdd() {
    setEditing(null)
    setDialogOpen(true)
  }

  function openEdit(e: Employee) {
    setEditing(e)
    setDialogOpen(true)
  }

  function onDeactivate(e: Employee) {
    deactivate.mutate(e.id, {
      onSuccess: () => toast.success(`"${e.fullName}" deactivated`),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faUsers} className="text-muted-foreground" />
            Employees
          </CardTitle>
          <CardDescription>Manage employee records used for payroll.</CardDescription>
        </div>
        <Button onClick={openAdd}>
          <FontAwesomeIcon icon={faUserPlus} className="mr-2" />
          Add Employee
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading && <p className="text-muted-foreground text-sm">Loading…</p>}
        {isError && (
          <p className="text-destructive text-sm">
            Could not load employees. Is the backend running on :8080?
          </p>
        )}
        {employees && (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Name</TableHead>
                <TableHead>Type</TableHead>
                <TableHead className="text-right">Rate</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {employees.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-muted-foreground text-center">
                    No employees yet — add your first one.
                  </TableCell>
                </TableRow>
              )}
              {employees.map((e) => (
                <TableRow key={e.id}>
                  <TableCell className="font-mono text-sm">{e.id}</TableCell>
                  <TableCell className="font-medium">{e.fullName}</TableCell>
                  <TableCell>{e.typeLabel}</TableCell>
                  <TableCell className="text-right">{rateOf(e)}</TableCell>
                  <TableCell>
                    <Badge variant={e.active ? 'default' : 'secondary'}>
                      {e.active ? 'Active' : 'Inactive'}
                    </Badge>
                  </TableCell>
                  <TableCell className="space-x-1 text-right">
                    <Button variant="ghost" size="sm" onClick={() => setLoansFor(e)}>
                      <FontAwesomeIcon icon={faMoneyCheckDollar} className="mr-1" />
                      Loans
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => setPayItemsFor(e)}>
                      <FontAwesomeIcon icon={faSackDollar} className="mr-1" />
                      Pay Items
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => openEdit(e)}>
                      <FontAwesomeIcon icon={faPen} className="mr-1" />
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      disabled={!e.active || deactivate.isPending}
                      onClick={() => onDeactivate(e)}
                    >
                      <FontAwesomeIcon icon={faBan} className="mr-1" />
                      Deactivate
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>

      <EmployeeFormDialog open={dialogOpen} onOpenChange={setDialogOpen} employee={editing} />
      <LoansDialog
        open={loansFor !== null}
        onOpenChange={(o) => !o && setLoansFor(null)}
        employee={loansFor}
      />
      <PayItemsDialog
        open={payItemsFor !== null}
        onOpenChange={(o) => !o && setPayItemsFor(null)}
        employee={payItemsFor}
      />
    </Card>
  )
}
