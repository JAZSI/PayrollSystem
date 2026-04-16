# UI & Main Flow

This part controls how the program starts, how the console menu works, and how the user is guided through payroll input. GUI is ignored for now.

## 1. Main

This is the program entry point. It only asks the user whether to run the CLI, GUI, or exit.

### What it does
- Shows the first menu
- Reads the user's choice
- Starts the CLI or GUI version
- Ends the program if the user chooses exit

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| main(args) | Starts the application and sends the user to the selected mode | String[] args | void |
| printLauncherMenu() | Prints the first menu shown at startup | none | void |

## 2. CLI Menu

This class contains the full console-based payroll flow.

### What it does
- Prints the CLI header
- Asks for employee details
- Asks for cut-off period
- Collects daily attendance records
- Asks for loan deduction
- Sends all data to the payroll calculator
- Prints the final payslip

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| main(args) | Runs the full CLI loop and lets the user process another payroll or exit | String[] args | void |
| printHeader() | Prints the top banner for the CLI | none | void |
| processPayroll() | Runs one complete payroll transaction from input to payslip | none | void |
| readEmployeeType() | Lets the user choose employee type | none | String |
| createEmployee() | Creates the correct employee object based on the selected type | none | Employee |
| readCutOffPeriod() | Lets the user choose the payroll cut-off period | none | String |
| getWorkingDays(cutOffPeriod) | Returns the working days for the selected cut-off period | String cutOffPeriod | int[] |
| readTimeRecord(dayNumber) | Reads one day of attendance and creates a time record | int dayNumber | TimeRecord |
| readAllTimeRecords(cutOffPeriod) | Reads all time records for the selected cut-off | String cutOffPeriod | TimeRecord[] |
| readLoanAmount() | Reads the loan deduction amount, or returns zero if none | none | double |
| printPayslip(entry) | Prints the final payroll summary in a formatted layout | PayrollEntry entry | void |
| formatPeso(amount) | Formats a number as Philippine Peso text | double amount | String |

## 3. InputValidator

This is a helper class for safe user input. It keeps the program from crashing when the user types invalid data.

### What it does
- Reads numbers safely
- Checks if values are within range
- Checks if text is not empty
- Validates employee IDs, names, yes/no answers, and HHMM time input

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| readIntInRange(scanner, prompt, min, max) | Reads an integer within a valid range | Scanner scanner, String prompt, int min, int max | int |
| readDoubleMin(scanner, prompt, min) | Reads a decimal number that is at least the minimum | Scanner scanner, String prompt, double min | double |
| readRequiredText(scanner, prompt) | Reads text that cannot be empty | Scanner scanner, String prompt | String |
| readEmployeeId(scanner, prompt) | Reads a valid employee ID made of digits and hyphens only | Scanner scanner, String prompt | String |
| readPersonName(scanner, prompt) | Reads a valid person name | Scanner scanner, String prompt | String |
| readEmployeeType(scanner, prompt) | Reads and validates the employee type code | Scanner scanner, String prompt | String |
| readYesNo(scanner, prompt) | Reads a yes or no answer | Scanner scanner, String prompt | boolean |
| readHHMM(scanner, prompt) | Reads a time value in HHMM format | Scanner scanner, String prompt | int |
| isValidHHMM(hhmm) | Checks whether the minute part is valid | int hhmm | boolean |

## 4. Test

This class is a fixed-data demo runner. It is not part of the normal user flow.

### What it does
- Creates a sample employee
- Builds sample time records
- Runs payroll calculation using hardcoded values
- Prints the payslip

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| main(args) | Runs the sample payroll test flow | String[] args | void |
| createEmployeeFromConstants() | Creates one employee using the fixed test values | none | Employee |
| buildConstantTimeRecords() | Builds sample time records from fixed data | none | TimeRecord[] |

### Note
- This class is useful for testing the payroll logic without typing data manually.
- It is not needed for normal payroll processing.