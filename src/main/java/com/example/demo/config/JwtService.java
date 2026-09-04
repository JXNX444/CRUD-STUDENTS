package com.example.demo.config;

import com.example.demo.modelo.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Clave para firmar los tokens (minimo 32 caracteres).
    // Ideal: moverla a variable de entorno. Por ahora aqui para simplificar.
    private static final String SECRET = "clave-secreta-super-larga-para-firmar-jwt-1234567890";

    // Duracion del token en milisegundos. 3600000 = 1 hora.
    private static final long EXPIRACION_MS = 3600000;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token para un usuario ya validado.
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + EXPIRACION_MS);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("rol", usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(getKey())
                .compact();
    }
}