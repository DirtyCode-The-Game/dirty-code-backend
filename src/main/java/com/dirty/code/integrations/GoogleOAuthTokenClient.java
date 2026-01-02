package com.dirty.code.integrations;

import com.dirty.code.integrations.domain.GoogleTokenRequest;
import com.dirty.code.integrations.domain.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "googleOAuthTokenClient", url = "https://oauth2.googleapis.com")
public interface GoogleOAuthTokenClient {

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GoogleTokenResponse exchangeCode(GoogleTokenRequest params);
}
