package com.noviq.backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Responsible for:
 * - generating JWTs after successful authentication
 * - validating incoming JWTs
 * - extracting claims from JWTs
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.signingKey = Keys.hmacShaKeyFor(
            jwtProperties
                .getSecret()
                .getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates a JWT token for the given user.
     */
    public String generateToken(UserDetails user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(
                    new Date(
                        System.currentTimeMillis()
                            + jwtProperties.getExpiration()
                    )
                )
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates a JWT against the given user.
     *
     * Returns false instead of throwing an exception when
     * the token is expired or otherwise invalid.
     */
    public boolean isTokenValid(
        String token,
        UserDetails user
    ) {

        try {

            String username = extractUsername(token);

            return username.equals(user.getUsername())
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    /**
     * Extracts the username from the JWT subject.
     */
    public String extractUsername(String token) {

        return extractClaim(
            token,
            Claims::getSubject
        );
    }

    /**
     * Checks whether the token has expired.
     */
    private boolean isTokenExpired(String token) {

        return extractClaim(
            token,
            Claims::getExpiration
        ).before(new Date());
    }

    /**
     * Extracts a claim from the JWT.
     */
    private <T> T extractClaim(
        String token,
        Function<Claims, T> claimsResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Parses and verifies the JWT.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns the JWT signing key.
     */
    private SecretKey getSigningKey() {

        return signingKey;
    }
}