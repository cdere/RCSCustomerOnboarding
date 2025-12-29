package com.rcs.rcscustomeronboarding.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console()) // Disable CSRF for H2
                        .disable()) // Disable for REST APIs (standard if using JWT/Stateless)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // Allow H2 UI
                        .requestMatchers("/api/v1/onboarding/customer/addCustomer").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/v1/onboarding/customer/**").hasAnyRole("TPM", "SALES", "ADMIN")
                        .requestMatchers("/api/v1/onboarding/search").hasAnyRole("TPM", "SALES", "ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // Required for H2 Console frames
                .httpBasic(Customizer.withDefaults()); // Simple auth for demo; use JWT for production

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Mocking users for testing logic
        UserDetails customer = User.withUsername("client_user")
                .password(passwordEncoder().encode("pass"))
                .roles("CUSTOMER").build();

        UserDetails tpm = User.withUsername("tpm_user")
                .password(passwordEncoder().encode("pass"))
                .roles("TPM").build();

        UserDetails sales = User.withUsername("tpm_sales")
                .password(passwordEncoder().encode("pass"))
                .roles("SALES").build();

        return new InMemoryUserDetailsManager(customer, tpm, sales);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
