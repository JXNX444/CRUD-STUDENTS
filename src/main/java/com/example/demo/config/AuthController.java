package com.example.demo.config;

import com.example.demo.modelo.Usuario;
import com.example.demo.modelo.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Lo que llega en el body del login: usuario y contrasena.
    public record LoginRequest(String username, String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos) {
        Optional<Usuario> encontrado = usuarioRepository.findByUsername(datos.username());

        if (encontrado.isEmpty()
                || !passwordEncoder.matches(datos.password(), encontrado.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario o contrasena incorrectos"));
        }

        String token = jwtService.generarToken(encontrado.get());
        return ResponseEntity.ok(Map.of("token", token));
    }
}