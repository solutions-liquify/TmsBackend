package app.tmsbackend.service

import app.tmsbackend.config.JwtUtil
import app.tmsbackend.model.AuthResponse
import app.tmsbackend.model.AuthUserDTO
import app.tmsbackend.repository.AuthUserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthUserService(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: UserDetailsServiceImpl
) {
    fun login(username: String, password: String): AuthResponse {
        val authToken = UsernamePasswordAuthenticationToken(username, password)
        authenticationManager.authenticate(authToken)
        val userDetails = userDetailsService.loadUserByUsername(username)
        val accessToken = jwtUtil.generateAccessToken(userDetails)
        val refreshToken = jwtUtil.generateRefreshToken(userDetails)
        val authUser = authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found with username: $username")
        val authUserToUpdate = authUser.copy(refreshToken = refreshToken)
        authUserRepository.update(authUserToUpdate)
        return AuthResponse(accessToken, refreshToken)
    }

    fun createAuthUser(
        id: String,
        username: String,
        role: String
    ): AuthUserDTO {
        val randomPassword = "123456" // TODO: Make it random string
        val passwordHash = passwordEncoder.encode(randomPassword)
        val authUser = AuthUserDTO(
            id = id,
            username = username,
            passwordHash = passwordHash,
            role = role
        )
        return authUserRepository.create(authUser)
    }
}