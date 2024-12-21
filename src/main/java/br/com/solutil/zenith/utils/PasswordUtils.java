package br.com.solutil.zenith.utils;

import java.security.SecureRandom;

public class PasswordUtils {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL;
    private static final SecureRandom random = new SecureRandom();

    public static String generateSecurePassword() {
        StringBuilder password = new StringBuilder(12);

        // Garantir pelo menos um de cada tipo
        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Preencher o resto da senha
        for (int i = 4; i < 12; i++) {
            password.append(PASSWORD_ALLOW.charAt(random.nextInt(PASSWORD_ALLOW.length())));
        }

        // Embaralhar a senha
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    public static String generateNumericSecurePassword() {
        int MAX_CAPACITY = 6;
        StringBuilder password = new StringBuilder(MAX_CAPACITY);

        // Preencher o resto da senha
        for (int i = 0; i < MAX_CAPACITY; i++) {
            password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        }

        // Embaralhar a senha
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}
