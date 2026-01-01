package com.dirty.code.service;

import com.dirty.code.controller.GmailAuthController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

@Service
public class GmailAuthService implements GmailAuthController {

    @Value("${gcp.client-id}")
    private String clientId;
    
    @Override
    public RedirectView redirectToGoogle() {
        String url = String.format("https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=http%%3A%%2F%%2Flocalhost%%3A3000%%2Fapi%%2Flogin&response_type=code&scope=openid%%20email%%20profile&access_type=offline&prompt=consent%%20select_account", clientId);
        return new RedirectView(url);
    }
}
