package com.example.demo.utili;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class HashUtil {  
    public static String sha256(String input) {  
        try {  
            // 获取SHA-256哈希函数实例  
            MessageDigest digest = MessageDigest.getInstance("SHA-256");  
            // 将输入字符串转换为字节数组  
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);  
            // 计算哈希值  
            byte[] hashBytes = digest.digest(inputBytes);  
            // 将哈希值转换为十六进制字符串  
            StringBuilder hexString = new StringBuilder();  
            for (byte b : hashBytes) {  
                String hex = Integer.toHexString(0xff & b);  
                if (hex.length() == 1) hexString.append('0');  
                hexString.append(hex);  
            }  
            return hexString.toString();  
        } catch (NoSuchAlgorithmException e) {  
            throw new RuntimeException("Hashing algorithm not found", e);  
        }  
    }  
}