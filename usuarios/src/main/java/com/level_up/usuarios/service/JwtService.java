package com.level_up.usuarios.service;

import com.level_up.usuarios.model.UsuarioModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Esto se hizo gracias a GEMINI 🗿 🤖

    // 🔑 Configuración: Obtén la clave secreta desde las propiedades de la aplicación
    // (Ej: application.properties o application.yml)
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Duración del token (Ej: 24 horas en milisegundos)
    private final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;


    // --- 1. GENERACIÓN DEL TOKEN ---

    /**
     * Genera un JWT usando la información esencial del usuario.
     * @param usuario El modelo de usuario autenticado.
     * @return El String del JWT.
     */
    public String generarToken(UsuarioModel usuario) {
        Map<String, Object> claims = new HashMap<>();
        // Añadir Claims específicos (datos que quieres codificar en el token)
        claims.put("idUsuario", usuario.getIdUsuario());
        claims.put("nombreUsuario", usuario.getNombreUsuario());

        return construirToken(claims, usuario.getCorreo());
    }

    /**
     * Construye el JWT final con claims, sujeto, fecha de emisión y expiración.
     */
    private String construirToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                // 'sub' es el sujeto, generalmente el identificador principal (correo)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // --- 2. EXTRACCIÓN Y VALIDACIÓN DEL TOKEN ---

    /**
     * Verifica si un token es válido.
     * @param token El JWT recibido.
     * @param usuario El modelo de usuario que se espera.
     * @return true si es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UsuarioModel usuario) {
        final String username = extractUsername(token);
        return (username.equals(usuario.getCorreo())) && !isTokenExpired(token);
    }

    /**
     * Extrae el 'subject' (correo electrónico) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims (cuerpo) del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // --- 3. CLAVE SECRETA ---

    /**
     * Decodifica y obtiene la clave de firma secreta.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}