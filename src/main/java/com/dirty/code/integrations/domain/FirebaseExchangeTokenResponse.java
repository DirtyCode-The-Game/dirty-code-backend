package com.dirty.code.integrations.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseExchangeTokenResponse {
    private String idToken;
    private String refreshToken;
    private String expiresIn;
}
