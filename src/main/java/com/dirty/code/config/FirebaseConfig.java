package com.dirty.code.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                String privateKeyFormatted = firebaseProperties.getPrivateKey().replace("\\n", "\n");

                String credentialsJson = String.format(
                        "{\n" +
                        "  \"type\": \"%s\",\n" +
                        "  \"project_id\": \"%s\",\n" +
                        "  \"private_key_id\": \"%s\",\n" +
                        "  \"private_key\": \"%s\",\n" +
                        "  \"client_email\": \"%s\",\n" +
                        "  \"client_id\": \"%s\",\n" +
                        "  \"auth_uri\": \"%s\",\n" +
                        "  \"token_uri\": \"%s\",\n" +
                        "  \"auth_provider_x509_cert_url\": \"%s\",\n" +
                        "  \"client_x509_cert_url\": \"%s\",\n" +
                        "  \"universe_domain\": \"%s\"\n" +
                        "}",
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
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao inicializar Firebase: " + e.getMessage());
        }
    }
}
