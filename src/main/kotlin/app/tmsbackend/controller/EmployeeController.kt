package app.tmsbackend.controller

import app.tmsbackend.model.Employee
import app.tmsbackend.service.EmployeeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/api/v1/employees")
@RestController
class EmployeeController(
    private val employeeService: EmployeeService
) {

    @PostMapping
    fun createEmployee(@RequestBody employee: Employee): ResponseEntity<Employee> {
        val createdEmployee = employeeService.createEmployee(employee)
        return ResponseEntity.ok(createdEmployee)
    }

    @PostMapping("/update")
    fun updateEmployee(@RequestBody employee: Employee): ResponseEntity<Employee> {
        val updatedEmployee = employeeService.updateEmployee(employee)
        return ResponseEntity.ok(updatedEmployee)
    }

    @GetMapping("/{id}")
    fun getEmployee(@PathVariable id: String): ResponseEntity<Employee> {
        val employee = employeeService.getEmployee(id)
        return ResponseEntity.ok(employee)
    }

    @GetMapping
    fun listEmployees(
        @RequestParam search: String,
        @RequestParam roles: List<String>,
        @RequestParam page: Int,
        @RequestParam size: Int
    ): ResponseEntity<List<Employee>> {
        val employees = employeeService.listEmployees(search, roles, page, size)
        return ResponseEntity.ok(employees)
    }
}