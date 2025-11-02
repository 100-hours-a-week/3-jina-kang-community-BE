package com.ktb.ktb_community.legal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/legal")
public class LegalController {

    @GetMapping("/privacy-legal")
    public String privacyLegal() {
        return "privacy-legal";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }
}
