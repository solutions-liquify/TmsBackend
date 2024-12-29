package app.tmsbackend.model

data class AuthUserDTO(
    val id: String,
    val username: String,
    val passwordHash: String,
    val role: String,
    val refreshToken: String? = null
)