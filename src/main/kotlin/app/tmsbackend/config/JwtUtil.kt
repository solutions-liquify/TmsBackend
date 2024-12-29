package app.tmsbackend.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {
    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.access.expiration}")
    private var accessTokenExpiration: Long = 0

    @Value("\${jwt.refresh.expiration}")
    private var refreshTokenExpiration: Long = 0


    private fun generateToken(userDetails: UserDetails, expiration: Long): String {
        val claims: MutableMap<String, Any> = HashMap()

        val role = userDetails.authorities.firstOrNull()?.authority ?: "STAFF"
        claims["role"] = role

        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration * 1000)) // seconds to milliseconds
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact()
    }


    fun generateAccessToken(userDetails: UserDetails): String {
        return generateToken(userDetails, accessTokenExpiration)
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        return generateToken(userDetails, refreshTokenExpiration)
    }

    private fun getSignKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun extractUsername(token: String): String? {
        return extractAllClaims(token).subject
    }

    fun extractRoles(token: String): List<String> {
        val claims = extractAllClaims(token)

        if ("role" in claims) {
            val role = claims["role"] as? String
            return listOfNotNull(role)
        } else {
            throw IllegalArgumentException("Role not found in the token")
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        return (extractUsername(token) == userDetails.username && !isTokenExpired(token))
    }
}