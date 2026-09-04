package com.example.demo.config;

import com.example.demo.modelo.Usuario;
import com.example.demo.modelo.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public record LoginRequest(String username, String password) {
    }

    // LOGIN: valida usuario/contrasena y deja los tokens en cookies.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos, HttpServletResponse response) {
        Optional<Usuario> encontrado = usuarioRepository.findByUsername(datos.username());

        if (encontrado.isEmpty()
                || !passwordEncoder.matches(datos.password(), encontrado.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario o contrasena incorrectos"));
        }

        Usuario usuario = encontrado.get();
        String token = jwtService.generarToken(usuario);
        String refresh = jwtService.generarRefreshToken(usuario);

        response.addCookie(crearCookie("token", token));
        response.addCookie(crearCookie("refreshToken", refresh));

        return ResponseEntity.ok(Map.of("mensaje", "Login correcto"));
    }

    // REFRESH: con el refreshToken valido, genera un nuevo token de acceso.
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refresh = leerCookie(request, "refreshToken");

        if (refresh == null || !jwtService.esTokenValido(refresh)) {
            return ResponseEntity.status(401).body(Map.of("error", "Sesion expirada"));
        }

        String username = jwtService.extraerUsername(refresh);
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
        }

        String nuevoToken = jwtService.generarToken(usuario.get());
        response.addCookie(crearCookie("token", nuevoToken));

        return ResponseEntity.ok(Map.of("mensaje", "Token renovado"));
    }

    // LOGOUT: borra las cookies.
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addCookie(borrarCookie("token"));
        response.addCookie(borrarCookie("refreshToken"));
        return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada"));
    }

    private Cookie crearCookie(String nombre, String valor) {
        Cookie cookie = new Cookie(nombre, valor);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 dias
        return cookie;
    }

    private Cookie borrarCookie(String nombre) {
        Cookie cookie = new Cookie(nombre, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    private String leerCookie(HttpServletRequest request, String nombre) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals(nombre)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}