package app.tmsbackend.controller

import app.tmsbackend.model.LoginRequest
import app.tmsbackend.service.AuthUserService
import app.tmsbackend.service.UserDetailsServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
class AuthUserController(
    private val authUserService: AuthUserService,
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any>{
        return ResponseEntity.ok(authUserService.login(loginRequest.email, loginRequest.password))
    }
}