package com.dirty.code.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class GcpService {

    @Value("${gcp.client-id}")
    private String clientId;

    @Value("${gcp.escaped-redirect-uri}")
    private String escapedRedirectUri;

    public String getGoogleAuthUrl() {
        log.info("Generating Google Auth URL");
        String url = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + escapedRedirectUri +
                "&response_type=code" +
                "&scope=openid%20email%20profile" +
                "&access_type=offline" +
                "&prompt=consent%20select_account";
        log.info("Google Auth URL generated successfully");
        return url;
    }

    public GoogleIdToken.Payload verifyGoogleIdToken(String googleIdTokenString) {
        log.info("Verifying Google ID token");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

        try {
            GoogleIdToken googleIdToken = verifier.verify(googleIdTokenString);
            if (googleIdToken == null) {
                log.error("Invalid Google ID token (null after verification)");
                throw new RuntimeException("Invalid Google ID token");
            }
            log.info("Google ID token verified successfully for user: {}", googleIdToken.getPayload().getEmail());
            return googleIdToken.getPayload();
        } catch (Exception exception) {
            log.error("Error verifying Google ID token", exception);
            throw new RuntimeException("Error verifying Google ID token", exception);
        }
    }
}
