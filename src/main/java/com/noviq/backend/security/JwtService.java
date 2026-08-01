package com.noviq.backend.security;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Responsible for:
 * - generating JWTs after successful authentication
 * - validating incoming JWTs
 * - extracting claims from JWTs
 *
 * This service does not authenticate users. Authentication is handled
 * by Spring Security's AuthenticationManager.
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;
    
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param user The user details for which the token is generated.
     * @return A JWT token as a String.
     */
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername()) // Use the username (email) as the subject of the token
                .issuedAt(new Date()) // Set the token's issued date to the current time
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtProperties.getExpiration()
                        )
                ) // Set the token's expiration date based on the configured expiration time
                .signWith(getSigningKey()) // Sign the token using the configured signing key
                .compact(); // Build and return the JWT as a compact string
    }


    /**
     * Validates the provided JWT token against the given user details.
     *
     * @param token The JWT token to validate.
     * @param user  The user details to compare against the token's subject.
     * @return true if the token is valid and matches the user; false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extracts the username (subject) from the provided JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The username (subject) extracted from the token.
     */
    public String extractUsername(String token) {
       return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks if the provided JWT token has expired.
     *
     * @param token The JWT token to check for expiration.
     * @return true if the token has expired; false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Extracts a specific claim from the provided JWT token using the given claims resolver function.
     *
     * @param token          The JWT token from which to extract the claim.
     * @param claimsResolver A function that defines how to extract the desired claim from the token's claims.
     * @param <T>            The type of the extracted claim.
     * @return The extracted claim of type T.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token The JWT token from which to extract claims.
     * @return The Claims object containing all claims extracted from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Use the signing key to verify the token's signature
                .build() 
                .parseSignedClaims(token) // Parse the token and extract the claims
                .getPayload(); // Return the claims extracted from the token
    }

    /**
     * Retrieves the signing key used for JWT operations.
     *
     * @return The SecretKey used for signing and verifying JWTs.
     */
    private SecretKey getSigningKey() {
        return signingKey;
    }
}
