package app.tmsbackend.model


data class Employee(
    val id: String?,
    val name: String,
    val email: String,
    val contactNumber: String,
    val role: String,
    val createdAt: Long?
)

data class EmployeeDTO(
    val id: String,
    val name: String,
    val email: String,
    val contactNumber: String,
    val role: String,
    val createdAt: Long
) {
    fun toEmployee(): Employee {
        return Employee(
            id = id,
            name = name,
            email = email,
            contactNumber = contactNumber,
            role =role,
            createdAt = createdAt
        )
    }
}