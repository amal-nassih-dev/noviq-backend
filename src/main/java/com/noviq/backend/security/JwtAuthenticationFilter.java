package com.noviq.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService
    ) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    )
        throws ServletException, IOException {

        String authHeader =
            request.getHeader("Authorization");

        /*
         * No Authorization header.
         *
         * Let Spring Security decide whether the endpoint
         * requires authentication.
         */
        if (
            authHeader == null ||
            !authHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
            authHeader.substring(7);

        try {

            String username =
                jwtService.extractUsername(token);

            /*
             * Only authenticate if there isn't already
             * an authentication in the SecurityContext.
             */
            if (
                username != null &&
                SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null
            ) {

                UserDetails userDetails =
                    userDetailsService
                        .loadUserByUsername(username);

                if (
                    jwtService.isTokenValid(
                        token,
                        userDetails
                    )
                ) {

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );

                    SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
                }
            }

        } catch (JwtException | IllegalArgumentException e) {

            /*
             * Token is invalid or expired.
             *
             * We deliberately do NOT authenticate the request.
             *
             * Spring Security will subsequently return 401
             * when the endpoint requires authentication.
             */

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}