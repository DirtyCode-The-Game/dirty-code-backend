package com.dirty.code.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GoogleTokenUtils {

    public static String getFirebaseUid(GoogleIdToken.Payload payload) {
        return "google:" + payload.getSubject();
    }

    public static String getName(GoogleIdToken.Payload payload) {
        return (String) payload.get("name");
    }

    public static String getEmail(GoogleIdToken.Payload payload) {
        return payload.getEmail();
    }

    public static String getPicture(GoogleIdToken.Payload payload) {
        return (String) payload.get("picture");
    }
}
