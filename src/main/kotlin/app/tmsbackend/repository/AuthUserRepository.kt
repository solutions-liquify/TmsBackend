package app.tmsbackend.repository

import app.tmsbackend.model.AuthUserDTO
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class AuthUserRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper<AuthUserDTO> { rs, _ ->
        AuthUserDTO(
            id = rs.getString("id"),
            username = rs.getString("username"),
            passwordHash = rs.getString("password_hash"),
            role = rs.getString("role"),
            refreshToken = rs.getString("refresh_token")
        )
    }

    fun create(authUser: AuthUserDTO): AuthUserDTO {
        val sql = "INSERT INTO auth_users (id, username, password_hash, role, refresh_token) VALUES (?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql, authUser.id, authUser.username, authUser.passwordHash, authUser.role, authUser.refreshToken)
        return authUser
    }

    fun update(authUser: AuthUserDTO): AuthUserDTO {
        val sql = "UPDATE auth_users SET username = ?, password_hash = ?, role = ?, refresh_token = ? WHERE id = ?"
        jdbcTemplate.update(sql, authUser.username, authUser.passwordHash, authUser.role, authUser.refreshToken, authUser.id)
        return authUser
    }

    fun findByUsername(username: String): AuthUserDTO? {
        val sql = "SELECT * FROM auth_users WHERE username = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper, username)
    }
}

