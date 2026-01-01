package com.dirty.code.controller;
    
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/gmail")
public interface GmailAuthController {

    @GetMapping("/auth-page")
    RedirectView redirectToGoogle();
}
