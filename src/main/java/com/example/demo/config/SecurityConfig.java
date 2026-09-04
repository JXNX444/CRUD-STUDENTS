package com.example.demo.config;

import com.example.demo.modelo.Rol;
import com.example.demo.modelo.Usuario;
import com.example.demo.modelo.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Sin sesiones: cada peticion se valida por el token.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publico: pagina de login y sus endpoints.
                        .requestMatchers("/login.html", "/api/auth/**", "/error").permitAll()
                        // Todo lo demas requiere token.
                        .anyRequest().authenticated()
                )
                // Si no hay token valido: la API responde 401; el navegador va al login.
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/api/")) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    } else {
                        response.sendRedirect("/login.html");
                    }
                }))
                // Nuestro filtro de token se ejecuta antes del de login por defecto.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Crea el usuario admin de prueba al arrancar, si no existe.
    @Bean
    public CommandLineRunner crearAdmin(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario("admin", encoder.encode("admin123"), Rol.ADMIN);
                repo.save(admin);
            }
        };
    }
}