package com.dirty.code.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;

    @PostConstruct
    public void initialize() {
        log.info("Initializing Firebase Application");
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                String privateKeyFormatted = firebaseProperties.getPrivateKey().replace("\\n", "\n");

                String credentialsJson = String.format(
                        """
                                {
                                  "type": "%s",
                                  "project_id": "%s",
                                  "private_key_id": "%s",
                                  "private_key": "%s",
                                  "client_email": "%s",
                                  "client_id": "%s",
                                  "auth_uri": "%s",
                                  "token_uri": "%s",
                                  "auth_provider_x509_cert_url": "%s",
                                  "client_x509_cert_url": "%s",
                                  "universe_domain": "%s"
                                }""",
                        firebaseProperties.getType(), 
                        firebaseProperties.getProjectId(), 
                        firebaseProperties.getPrivateKeyId(), 
                        privateKeyFormatted.replace("\n", "\\n"),
                        firebaseProperties.getClientEmail(), 
                        firebaseProperties.getClientId(), 
                        firebaseProperties.getAuthUri(), 
                        firebaseProperties.getTokenUri(), 
                        firebaseProperties.getAuthProviderX509CertUrl(), 
                        firebaseProperties.getClientX509CertUrl(), 
                        firebaseProperties.getUniverseDomain()
                );

                try (java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(credentialsJson.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(is))
                            .setProjectId(firebaseProperties.getProjectId())
                            .build();

                    FirebaseApp.initializeApp(options);
                    log.info("Firebase Application initialized successfully for project: {}", firebaseProperties.getProjectId());
                }
            } else {
                log.info("Firebase Application already initialized");
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase", e);
        }
    }
}
