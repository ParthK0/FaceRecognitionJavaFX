package com.myapp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Util {
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append("%02x".formatted(b));
            return sb.toString();
        } catch (Exception e) { return null; }
    }
}
