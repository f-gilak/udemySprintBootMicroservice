package com.example.photoapp.accounts.ui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private Environment environment;

    @GetMapping("/status/check")
    public String status() {
        log.info("check status on port:{}", environment.getProperty("local.server.port"));
        return "Working on port: " + environment.getProperty("local.server.port") + ", with token=" +
                environment.getProperty("token.secret_key");
    }
}
