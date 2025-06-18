package by.base.main.service.util;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class PasswordGenerator {
    /**
     * <br>Генерация паролей</br>.
     * @author Ira
     */
    public String generatePassword(int length) {

        final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        final String NUMBER = "0123456789";
        final String OTHER_CHAR = "!@#$%&*()_+-=[]?";
        final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        final Random random = new SecureRandom();

        StringBuilder passwordBuilder = new StringBuilder(length);
        passwordBuilder.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        passwordBuilder.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        passwordBuilder.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        passwordBuilder.append(OTHER_CHAR.charAt(random.nextInt(OTHER_CHAR.length())));

        for (int i = 4; i < length; i++) {
            passwordBuilder.append(PASSWORD_ALLOW_BASE.charAt(random.nextInt(PASSWORD_ALLOW_BASE.length())));
        }
        return passwordBuilder.toString();
    }
}
