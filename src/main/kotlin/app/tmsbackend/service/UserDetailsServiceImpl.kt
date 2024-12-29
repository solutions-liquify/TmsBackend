package app.tmsbackend.service

import app.tmsbackend.model.AuthUserDTO
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl() : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val authUserDTO = AuthUserDTO(
            id = "something",
            username = "aakash@email.com",
            passwordHash = "somdflksj",
            role = "ADMIN",
            refreshToken = null,
        )

        return User(
            authUserDTO.username,
            authUserDTO.passwordHash,
            listOf(SimpleGrantedAuthority("ROLE_${authUserDTO.role}"))
        )
    }
}