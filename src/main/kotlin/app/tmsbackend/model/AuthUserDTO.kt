package app.tmsbackend.model

data class AuthUserDTO(
    val id: String,
    val username: String,
    val passwordHash: String,
    val role: String,
    val refreshToken: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)