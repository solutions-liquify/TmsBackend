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


    fun refreshToken(refreshToken: String): AuthResponse {
        val username = jwtUtil.extractUsername(refreshToken) ?: throw IllegalArgumentException("Invalid refresh token: Username extraction failed")
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw IllegalStateException("Expired token for user: $username")
        }

        val userDetails = userDetailsService.loadUserByUsername(username)
        val authUser = authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found with username: $username")

        if (authUser.refreshToken != refreshToken) {
            throw IllegalStateException("Invalid token for user: $username")
        }
        val newAccessToken = jwtUtil.generateAccessToken(userDetails)
        val newRefreshToken = jwtUtil.generateRefreshToken(userDetails)
        authUserRepository.update(authUser.copy(refreshToken = newRefreshToken))
        return AuthResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }

    fun createAuthUser(
        id: String,
        username: String,
        role: String
    ): AuthUserDTO {
        val randomPassword = (100000..999999).random().toString()
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