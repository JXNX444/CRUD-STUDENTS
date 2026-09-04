package com.example.demo.config;

import com.example.demo.modelo.Rol;
import com.example.demo.modelo.Usuario;
import com.example.demo.modelo.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()   // login abierto
                        .anyRequest().permitAll()                      // resto abierto (por ahora)
                );
        return http.build();
    }

    // Encripta y compara contrasenas con BCrypt.
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