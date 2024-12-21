package br.com.solutil.zenith.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newPassword = "zenith@2024"; // New password for admin
        String encodedPassword = encoder.encode(newPassword);
        System.out.println("New encoded password: " + encodedPassword);
    }
}
