package com.dirty.code.integrations.firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseExchangeTokenRequest {
    private String token;
    @Builder.Default
    private boolean returnSecureToken = true;
}
