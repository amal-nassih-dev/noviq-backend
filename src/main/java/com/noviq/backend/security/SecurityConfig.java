package com.noviq.backend.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider with the user details service and password encoder.
     *
     * @param userDetailsService The user details service to retrieve user information.
     * @param passwordEncoder    The password encoder to hash and verify passwords.
     * @return A configured DaoAuthenticationProvider.
     */
     @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
    
    /**
     * Configures the authentication manager using the provided authentication configuration.
     *
     * @param configuration The authentication configuration to retrieve the authentication manager.
     * @return The configured AuthenticationManager.
     * @throws Exception If an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }


    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs if needed
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/api/public/**").permitAll() // Public endpoints
    //             .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin-only endpoints
    //             .anyRequest().authenticated() // Everything else requires auth
    //         )
    //         .httpBasic(Customizer.withDefaults()); // Enables Basic Auth (or change to JWT)

    //     return http.build();
    // }

    /**
     * Configures the security filter chain for HTTP requests.
     * Disables CSRF protection and sets up authorization rules for different endpoints.
     *
     * @param http The HttpSecurity object to configure.
     * @param jwtAuthenticationFilter The JWT authentication filter to validate JWT tokens.
     * @param authenticationProvider The authentication provider to handle user authentication.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults()) // Without it, Spring Security blocks the browser's preflight request.
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider)
            .addFilterBefore( // add the JWT authentication filter before the UsernamePasswordAuthenticationFilter in the filter chain
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }

    
}
