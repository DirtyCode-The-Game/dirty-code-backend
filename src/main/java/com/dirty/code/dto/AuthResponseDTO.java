package com.dirty.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String uid;
    private String name;
    private String email;
    private String photoUrl;
    private String firebaseToken;
}
