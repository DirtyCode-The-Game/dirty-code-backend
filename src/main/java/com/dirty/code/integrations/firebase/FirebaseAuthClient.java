package com.dirty.code.integrations.firebase;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "firebaseAuthClient", url = "https://identitytoolkit.googleapis.com/v1")
public interface FirebaseAuthClient {

    @PostMapping("/accounts:signInWithCustomToken")
    FirebaseExchangeTokenResponse exchangeCustomToken(
            @RequestParam("key") String apiKey,
            @RequestBody FirebaseExchangeTokenRequest request
    );
}
