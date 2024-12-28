package app.tmsbackend.repository

import app.tmsbackend.model.Employee
import app.tmsbackend.model.EmployeeDTO
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class EmployeeRepository(private val jdbcTemplate: JdbcTemplate) {

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
        return employeeDTO
    }

    /**
     * Update employee
     * @param employeeDTO: EmployeeDTO
     * @return EmployeeDTO
     */
    fun updateEmployee(employeeDTO: EmployeeDTO): EmployeeDTO {
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
        return employeeDTO
    }


    /**
     * Get employee by id
     * @param id: String
     * @return EmployeeDTO?
     */ 
    fun getEmployee(id: String): EmployeeDTO? {
        val sql = "SELECT * FROM employees WHERE id = ?"
        val employee = jdbcTemplate.queryForObject(sql, { rs, _ -> rowMapper(rs) }, id)
        return employee
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
        val sqlBuilder = StringBuilder("SELECT * FROM employees WHERE 1=1")
        
        if (roles.isNotEmpty()) {
            sqlBuilder.append(" AND role IN (${roles.joinToString(",") { "'$it'" }})")
        }
        
        if (search.isNotBlank()) {
            sqlBuilder.append(" AND name LIKE ?")
        }
        
        sqlBuilder.append(" LIMIT ? OFFSET ?")
        
        val sql = sqlBuilder.toString()
        val offset = (page - 1) * size
        val employees = if (search.isNotBlank()) {
            jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, "%$search%", size, offset)
        } else {
            jdbcTemplate.query(sql, { rs, _ -> rowMapper(rs) }, size, offset)
        }
        
        return employees
    }

}