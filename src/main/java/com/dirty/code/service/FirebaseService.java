package com.dirty.code.service;

import com.dirty.code.config.FirebaseProperties;
import com.dirty.code.integrations.firebase.FirebaseAuthClient;
import com.dirty.code.integrations.firebase.FirebaseExchangeTokenRequest;
import com.dirty.code.integrations.firebase.FirebaseExchangeTokenResponse;
import com.dirty.code.utils.GoogleTokenUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseService {

    private final FirebaseProperties firebaseProperties;
    private final FirebaseAuthClient firebaseAuthClient;

    public UserRecord getOrCreateFirebaseUser(String firebaseUid, GoogleIdToken.Payload googlePayload) {
        log.info("Checking or creating Firebase user for UID: {}", firebaseUid);
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(firebaseUid);
            log.info("Firebase user found for UID: {}", firebaseUid);
            return userRecord;
        } catch (Exception ignored) {
            try {
                log.info("Creating new Firebase user for UID: {}", firebaseUid);
                UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                        .setUid(firebaseUid)
                        .setEmail(GoogleTokenUtils.getEmail(googlePayload))
                        .setEmailVerified(Boolean.TRUE.equals(googlePayload.getEmailVerified()))
                        .setDisplayName(GoogleTokenUtils.getName(googlePayload))
                        .setPhotoUrl(GoogleTokenUtils.getPicture(googlePayload));

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
                log.info("Successfully created Firebase user for UID: {}", firebaseUid);
                return userRecord;
            } catch (Exception exception) {
                log.error("Failed to create Firebase user for UID: {}", firebaseUid, exception);
                throw new RuntimeException("Error creating Firebase user", exception);
            }
        }
    }

    public String createFirebaseIdToken(String firebaseUid, GoogleIdToken.Payload googlePayload) {
        log.info("Creating Firebase ID token for UID: {}", firebaseUid);
        String customToken = createFirebaseCustomToken(firebaseUid, googlePayload);
        
        FirebaseExchangeTokenResponse response = firebaseAuthClient.exchangeCustomToken(
                firebaseProperties.getApiKey(),
                FirebaseExchangeTokenRequest.builder().token(customToken).build()
        );
        
        log.info("Successfully created Firebase ID token for UID: {}", firebaseUid);
        return response.getIdToken();
    }

    private String createFirebaseCustomToken(String firebaseUid, GoogleIdToken.Payload googlePayload) {
        log.info("Creating Firebase custom token for UID: {}", firebaseUid);
        try {
            Map<String, Object> customClaims = new HashMap<>();
            customClaims.put("provider", "google");
            customClaims.put("email", GoogleTokenUtils.getEmail(googlePayload));

            String customToken = FirebaseAuth.getInstance().createCustomToken(firebaseUid, customClaims);
            log.info("Successfully created Firebase custom token for UID: {}", firebaseUid);
            return customToken;
        } catch (Exception exception) {
            log.error("Failed to create Firebase custom token for UID: {}", firebaseUid, exception);
            throw new RuntimeException("Error creating Firebase custom token", exception);
        }
    }
}
