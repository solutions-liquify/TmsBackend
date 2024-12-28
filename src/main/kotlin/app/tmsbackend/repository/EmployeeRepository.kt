package app.tmsbackend.repository

import app.tmsbackend.model.Employee
import app.tmsbackend.model.EmployeeDTO
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.slf4j.LoggerFactory

@Repository
class EmployeeRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(EmployeeRepository::class.java)

    /**
     * Row mapper for EmployeeDTO
     * @param rs: java.sql.ResultSet
     * @return EmployeeDTO
     */
    private fun rowMapper(rs: java.sql.ResultSet): EmployeeDTO {
        return EmployeeDTO(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("contact_number"),
            rs.getString("role"),
            rs.getLong("created_at")
        )
    }
    
    /**
     * Create employee
     * @param employeeDTO: EmployeeDTO
     * @return EmployeeDTO
     */
    fun createEmployee(employeeDTO: EmployeeDTO): EmployeeDTO {
        try {
            logger.debug("[APP] Creating employee with ID: ${employeeDTO.id}")
            val sql = "INSERT INTO employees (id, name, email, contact_number, role, created_at) VALUES (?, ?, ?, ?, ?, ?)"
            jdbcTemplate.update(
                sql,
                employeeDTO.id,
                employeeDTO.name,
                employeeDTO.email,
                employeeDTO.contactNumber,
                employeeDTO.role,
                employeeDTO.createdAt
            )
            logger.debug("[APP] Employee created with ID: ${employeeDTO.id}")
            return employeeDTO
        } catch (e: Exception) {
            logger.error("[APP] Error creating employee with ID: ${employeeDTO.id}", e)
            throw e
        }
    }

    /**
     * Update employee
     * @param employeeDTO: EmployeeDTO
     * @return EmployeeDTO
     */
    fun updateEmployee(employeeDTO: EmployeeDTO): EmployeeDTO {
        try {
            logger.debug("[APP] Updating employee with ID: ${employeeDTO.id}")
            val sql = "UPDATE employees SET name = ?, email = ?, contact_number = ?, role = ?, created_at = ? WHERE id = ?"
            jdbcTemplate.update(
                sql,
                employeeDTO.name,
                employeeDTO.email,
                employeeDTO.contactNumber,
                employeeDTO.role,
                employeeDTO.createdAt,
                employeeDTO.id
            )
            logger.debug("[APP] Employee updated with ID: ${employeeDTO.id}")
            return employeeDTO
        } catch (e: Exception) {
            logger.error("[APP] Error updating employee with ID: ${employeeDTO.id}", e)
            throw e
        }
    }

    /**
     * Get employee by id
     * @param id: String
     * @return EmployeeDTO?
     */ 
    fun getEmployee(id: String): EmployeeDTO? {
        try {
            logger.debug("[APP] Fetching employee with ID: $id")
            val sql = "SELECT * FROM employees WHERE id = ?"
            val employee = jdbcTemplate.queryForObject(sql, { rs, _ -> rowMapper(rs) }, id)
            if (employee != null) {
                logger.info("[APP] Employee found with ID: $id")
            } else {
                logger.warn("[APP] No employee found with ID: $id")
            }
            return employee
        } catch (e: Exception) {
            logger.error("[APP] Error fetching employee with ID: $id", e)
            throw e
        }
    } 
    
    /**
     * List employees with pagination and search
     * @param search: String
     * @param roles: List<String>   
     * @param page: Int
     * @param size: Int
     * @return List<EmployeeDTO>
     */
    
    fun listEmployees(search: String, roles: List<String>, page: Int, size: Int): List<EmployeeDTO> {
        try {
            logger.debug("[APP] Listing employees with search: '$search', roles: $roles, page: $page, size: $size")
            val sqlBuilder = StringBuilder("SELECT * FROM employees WHERE 1=1")
            
            if (roles.isNotEmpty()) {
                sqlBuilder.append(" AND role IN (${roles.joinToString(",") { "'$it'" }})")
            }
            
            if (search.isNotBlank()) {
                sqlBuilder.append(" AND name LIKE ?")
            }
            
            sqlBuilder.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?")
            
            val sql = sqlBuilder.toString()
            val offset = (page - 1) * size
            val employees = if (search.isNotBlank()) {
                jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, "%$search%", size, offset)
            } else {
                jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, size, offset)
            }
            
            logger.info("[APP] Listed ${employees.size} employees")
            return employees
        } catch (e: Exception) {
            logger.error("[APP] Error listing employees with search: '$search', roles: $roles, page: $page, size: $size", e)
            throw e
        }
    }

}