package com.cibertec.util; // Puedes ponerla donde quieras

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Para HMAC-SHA256 (256 bits)
        // byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();

        // Para HMAC-SHA512 (512 bits), que es más seguro y lo que jjwt prefiere si tu clave es larga
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();

        String base64EncodedKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Nueva clave secreta JWT (Base64 codificada - HS512):");
        System.out.println(base64EncodedKey);
    }
}
