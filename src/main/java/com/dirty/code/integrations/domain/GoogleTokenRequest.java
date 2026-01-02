package com.dirty.code.integrations.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class GoogleTokenRequest {
    private String code;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @Builder.Default
    @JsonProperty("redirect_uri")
    private String redirectUri = "http://localhost:8080/dirty-code/v1/gmail/call-back";

    @Builder.Default
    @JsonProperty("grant_type")
    private String grantType = "authorization_code";
}
