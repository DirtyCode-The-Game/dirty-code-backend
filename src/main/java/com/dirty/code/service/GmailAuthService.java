package com.dirty.code.service;

import com.dirty.code.config.FirebaseProperties;
import com.dirty.code.controller.GmailAuthController;
import com.dirty.code.integrations.GoogleOAuthTokenClient;
import com.dirty.code.integrations.domain.FirebaseExchangeTokenResponse;
import com.dirty.code.integrations.domain.GoogleTokenRequest;
import com.dirty.code.integrations.domain.GoogleTokenResponse;
import com.dirty.code.utils.GoogleTokenUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailAuthService implements GmailAuthController {

    @Value("${gcp.client-id}")
    private String clientId;

    @Value("${gcp.client-secret}")
    private String clientSecret;

    @Value("${gcp.redirect-uri}")
    private String redirectUri;

    @Value("${app.frontend.login-url}")
    private String frontendLoginUrl;

    private final GoogleOAuthTokenClient googleOAuthTokenClient;
    private final UserService userService;
    private final GcpService gcpService;
    private final FirebaseService firebaseService;
    private final FirebaseProperties firebaseProperties;

    @Override
    public RedirectView redirectToGoogle() {
        if (!firebaseProperties.isEnabled()) {
            log.info("Offline mode: Redirecting directly to callback with mock code");
            return new RedirectView("/dirty-code/v1/gmail/call-back?code=mock-code");
        }
        log.info("Redirecting to Google login page");
        RedirectView redirectView = new RedirectView(gcpService.getGoogleAuthUrl());
        log.info("Redirect URL prepared");
        return redirectView;
    }

    @Override
    public RedirectView gmailCallBack(String code) {
        log.info("Processing Gmail callback, code: {}", code);
        String firebaseUid;
        GoogleIdToken.Payload googlePayload;

        if (firebaseProperties.isEnabled()) {
            GoogleTokenResponse tokenResponse = googleOAuthTokenClient.exchangeCode(GoogleTokenRequest.builder()
                    .code(code)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUri)
                    .build());

            googlePayload = gcpService.verifyGoogleIdToken(tokenResponse.getIdToken());
            firebaseUid = GoogleTokenUtils.getFirebaseUid(googlePayload);
            log.info("Processing callback for user: {}", googlePayload.getEmail());

            UserRecord firebaseUser = firebaseService.getOrCreateFirebaseUser(firebaseUid, googlePayload);
            userService.saveOrUpdateUser(firebaseUser);

            FirebaseExchangeTokenResponse firebaseToken = firebaseService.createFirebaseIdToken(firebaseUid, googlePayload);
            return new RedirectView(String.format(frontendLoginUrl, firebaseToken.getIdToken(), firebaseToken.getRefreshToken()));
        } else {
            log.info("Offline mode: Creating mock user and tokens");
            firebaseUid = "mock-token-123";
            userService.saveOrUpdateUser(firebaseUid, "mock-user@example.com", "Mock User", "http://example.com/photo.jpg");
            
            return new RedirectView(String.format(frontendLoginUrl, "mock-token-123", "mock-token-123"));
        }
    }
}
