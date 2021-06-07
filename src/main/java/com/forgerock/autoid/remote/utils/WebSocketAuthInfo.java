package com.forgerock.autoid.remote.utils;

import java.util.UUID;

public class WebSocketAuthInfo {
    UUID authToken;

    public WebSocketAuthInfo() {
    }

    public WebSocketAuthInfo(UUID authToken) {
        this.authToken = authToken;
    }

    public UUID getAuthToken() {
        return authToken;
    }
}