package com.example.oplogy;

import java.util.UUID;

public class CreateUUID {

    public static String generateUUID() {
        // UUIDを生成する処理
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}