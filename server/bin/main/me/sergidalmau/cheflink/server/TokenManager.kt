package me.sergidalmau.cheflink.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import java.util.*

class TokenManager(env: Dotenv) {
    private val secret = env["JWT_SECRET"] ?: "default-secret"
    private val issuer = env["JWT_ISSUER"] ?: "cheflink-server"
    private val audience = env["JWT_AUDIENCE"] ?: "cheflink-clients"
    
    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    // 15 minutes for Access Token
    fun generateAccessToken(userId: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 15 * 60 * 1000))
            .sign(algorithm)
    }

    // 7 days for Refresh Token
    fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
            .sign(algorithm)
    }
    
    fun getVerifier() = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}
