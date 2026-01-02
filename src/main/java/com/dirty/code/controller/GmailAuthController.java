package com.dirty.code.controller;
    
import com.dirty.code.dto.AuthResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/v1/gmail")
public interface GmailAuthController {

    @GetMapping("/auth-page")
    RedirectView redirectToGoogle();

    @GetMapping("/call-back")
    RedirectView gmailCallBack(@RequestParam(required = false) String code);

}
