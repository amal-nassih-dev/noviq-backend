package com.noviq.backend.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

import java.io.IOException;

/**
 * JwtAuthenticationFilter is a custom filter that intercepts incoming HTTP requests to validate JWT tokens.
 * It checks for the presence of a JWT in the Authorization header, validates it, and sets the authentication
 * in the Spring Security context if the token is valid.
 * It does not handle authentication itself; it relies on the JwtService for token validation and extraction of user details.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) { // If the Authorization header is missing or doesn't start with "Bearer ", continue the filter chain without authentication
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7); // Extract the JWT from the Authorization header because it starts with "Bearer "
        try {
            String username = jwtService.extractUsername(jwt); // Extract the username from the JWT
            if (SecurityContextHolder.getContext().getAuthentication() == null) { // If the user is not already authenticated, proceed with authentication
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, user)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken( // Create an authentication token with the user details and authorities
                            user,
                            null,
                            user.getAuthorities()
                    );
                authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request)); // Set additional details for the authentication token, such as the remote address and session ID
                SecurityContextHolder.getContext().setAuthentication(authToken); // Set the authentication in the security context, allowing the user to be authenticated for this request
                // after this line the user is authenticated and can access secured endpoints based on their roles and permissions, and Spring will know infos about the user and their roles for the current request.
                // we can use @AuthenticationPrincipal User user or SecurityContextHolder.getContext().getAuthentication().getPrincipal() to get the user details in the controller.
                }

            }
        } catch (JwtException ex) {
            filterChain.doFilter(request, response);
            return;
        }  

        filterChain.doFilter(request, response); // Continue the filter chain after processing the JWT
    } 
}
