package app.tmsbackend.service

import app.tmsbackend.model.Employee
import app.tmsbackend.model.EmployeeDTO
import app.tmsbackend.repository.EmployeeRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class EmployeeService(private val employeeRepository: EmployeeRepository) {

    /**
     * Create employee
     * sets id and createdAt
     * @param employee: Employee
     * @return Employee
     */
    fun createEmployee(employee: Employee): Employee {

        val employeeToCreate: Employee = employee.copy(
            id = UUID.randomUUID().toString(),
            createdAt = Instant.now().epochSecond
        )

        val employeeDTO = EmployeeDTO(
            employeeToCreate.id ?: "",
            employeeToCreate.name,
            employeeToCreate.email,
            employeeToCreate.contactNumber,
            employeeToCreate.role,
            employeeToCreate.createdAt ?: 0L
        )
        employeeRepository.createEmployee(employeeDTO)
        return employeeToCreate
    }

    /**
     * Update employee
     * @param employee: Employee
     * @return Employee
     */
    fun updateEmployee(employee: Employee): Employee {
        val employeeDTO = EmployeeDTO(
            employee.id ?: throw IllegalArgumentException("Employee ID must be present"),
            employee.name,
            employee.email,
            employee.contactNumber,
            employee.role,
            employee.createdAt ?: 0L
        )
        employeeRepository.updateEmployee(employeeDTO)
        return employee
    }

    /**
     * Get employee by id
     * @param id: String
     * @return Employee
     */
    fun getEmployee(id: String): Employee {
        val employeeDTO = employeeRepository.getEmployee(id) ?: throw IllegalArgumentException("Employee not found")
        return Employee(
            employeeDTO.id,
            employeeDTO.name,
            employeeDTO.email,
            employeeDTO.contactNumber,
            employeeDTO.role,
            employeeDTO.createdAt
        )
    }

    /**
     * List employees with pagination and search
     * @param search: String
     * @param roles: List<String>
     * @param page: Int
     * @param size: Int
     * @return List<Employee>
     */
    fun listEmployees(
        search: String, 
        roles: List<String>, 
        page: Int, 
        size: Int
    ): List<Employee> {
        val employeeDTOs = employeeRepository.listEmployees(search, roles, page, size)
        return employeeDTOs.map { dto ->
            Employee(
                dto.id,
                dto.name,
                dto.email,
                dto.contactNumber,
                dto.role,
                dto.createdAt
            )
        }
    }
}



