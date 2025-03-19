package by.base.main.service.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Утилитарный класс используется для шифрования и дешифрования информации
 */
@Service
public class AESCryptoUtil {

	private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // 128 бит
    private static final int IV_SIZE = 16; // 16 байт IV
    
    @Value("${encryption.secret}")  // Загружаем ключ из application.properties
    private String secretKeyBase64;
    
    private SecretKey secretKey;
    
    @PostConstruct
    public void init() {
        // Декодируем ключ из Base64
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        
//        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        System.out.println("Скопируйте этот ключ и добавьте в application.properties:");
//        System.out.println("encryption.secret=" + encodedKey);
    }

    /**
     * Генерация случайного секретного ключа (используется один раз)
     * @return
     * @throws Exception
     */
    @Deprecated
    public SecretKey generateKey() throws Exception {
    	if(secretKey == null) {
    		KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(256); // Используем 256-битный ключ
            return keyGenerator.generateKey();
    	}else {
    		return secretKey;
    	}
        
    }

    /**
     * Шифрование ID
     * @param plainText
     * @param secretKey
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Объединяем IV + шифртекст и кодируем в Base64
        byte[] combined = new byte[IV_SIZE + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, IV_SIZE);
        System.arraycopy(encryptedBytes, 0, combined, IV_SIZE, encryptedBytes.length);

        return Base64.getUrlEncoder().encodeToString(combined);
    }

    // Дешифрование ID
    public String decrypt(String encryptedText) throws Exception {
        byte[] combined = Base64.getUrlDecoder().decode(encryptedText);
        
        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedBytes = new byte[combined.length - IV_SIZE];

        System.arraycopy(combined, 0, iv, 0, IV_SIZE);
        System.arraycopy(combined, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public void main() throws Exception {
        // 1️⃣ Генерируем ключ (сохраните его, чтобы потом использовать для расшифровки)
        SecretKey secretKey = generateKey();
        
        // 🔹 Пример: шифруем id = "12345"
        String encryptedId = encrypt("12345");
        System.out.println("🔐 Зашифрованный ID: " + encryptedId);

        // 🔓 Дешифруем обратно
        String decryptedId = decrypt(encryptedId);
        System.out.println("🔓 Расшифрованный ID: " + decryptedId);
    }
}
