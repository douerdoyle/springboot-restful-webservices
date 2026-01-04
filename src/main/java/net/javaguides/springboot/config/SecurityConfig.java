package net.javaguides.springboot.config;

import net.javaguides.springboot.controller.ApiPaths;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties(AppSecurityProperties.class)
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_VIEWER = "VIEWER";
    private static final String[] SWAGGER_PATHS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AppSecurityProperties properties, PasswordEncoder passwordEncoder) {
        List<UserDetails> adminUsers = properties.getAdmins().stream()
                .map(credentials -> buildUser(credentials, ROLE_ADMIN, passwordEncoder))
                .toList();

        List<UserDetails> viewerUsers = properties.getViewers().stream()
                .map(credentials -> buildUser(credentials, ROLE_VIEWER, passwordEncoder))
                .toList();

        List<UserDetails> users = Stream.concat(adminUsers.stream(), viewerUsers.stream()).toList();

        return new InMemoryUserDetailsManager(users);
    }

    private UserDetails buildUser(AppSecurityProperties.UserCredentials credentials, String role, PasswordEncoder passwordEncoder) {
        return User.withUsername(credentials.getUsername())
                .password(passwordEncoder.encode(credentials.getPassword()))
                .roles(role)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_PATHS).hasRole(ROLE_ADMIN)
                        .requestMatchers("/actuator/**").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, ApiPaths.API_V2_BASE + "/**").hasAnyRole(ROLE_ADMIN, ROLE_VIEWER)
                        .requestMatchers(ApiPaths.API_V2_BASE + "/**").hasRole(ROLE_ADMIN)
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
