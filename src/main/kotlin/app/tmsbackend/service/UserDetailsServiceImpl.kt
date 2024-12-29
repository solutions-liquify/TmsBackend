package app.tmsbackend.service

import app.tmsbackend.model.AuthUserDTO
import app.tmsbackend.repository.AuthUserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val authUserRepository: AuthUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val authUserDTO = authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found with username: $username")

        return User(
            authUserDTO.username,
            authUserDTO.passwordHash,
            listOf(SimpleGrantedAuthority("ROLE_${authUserDTO.role}"))
        )
    }
}