package com.cibertec.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password"; // La contraseña en texto plano que usarás
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Nuevo hash BCrypt para '" + rawPassword + "':");
        System.out.println(encodedPassword);
    }
}
