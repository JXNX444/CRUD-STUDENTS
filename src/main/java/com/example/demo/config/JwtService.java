package com.example.demo.config;

import com.example.demo.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Clave para firmar los tokens (minimo 32 caracteres).
    private static final String SECRET = "clave-secreta-super-larga-para-firmar-jwt-1234567890";

    // Token de acceso: dura 1 minuto (60000 ms), como pidio el profesor.
    private static final long ACCESO_MS = 60000;

    // Refresh token: dura 7 dias.
    private static final long REFRESH_MS = 7L * 24 * 60 * 60 * 1000;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Genera el token de acceso (corto).
    public String generarToken(Usuario usuario) {
        return construir(usuario.getUsername(), usuario.getRol().name(), ACCESO_MS);
    }

    // Genera el refresh token (largo).
    public String generarRefreshToken(Usuario usuario) {
        return construir(usuario.getUsername(), usuario.getRol().name(), REFRESH_MS);
    }

    private String construir(String username, String rol, long duracionMs) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + duracionMs);
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(getKey())
                .compact();
    }

    // Saca el username de adentro del token.
    public String extraerUsername(String token) {
        return leerClaims(token).getSubject();
    }

    // Devuelve true si el token es valido y no esta vencido.
    public boolean esTokenValido(String token) {
        try {
            leerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}