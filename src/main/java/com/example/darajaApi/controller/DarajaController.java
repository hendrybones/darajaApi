package com.example.darajaApi.controller;

import com.example.darajaApi.data.AccessTokenResponse;
import com.example.darajaApi.data.RegisterUrlResponse;
import com.example.darajaApi.service.DarajaApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mobile-money")
public class DarajaController {

    private final DarajaApiService darajaApiService;

    public DarajaController(DarajaApiService darajaApiService) {
        this.darajaApiService = darajaApiService;
    }

    @GetMapping(path = "/token", produces = "application/json")
    public ResponseEntity<AccessTokenResponse>getAccessToken(){
        return ResponseEntity.ok(darajaApiService.getAccessToken());
    }
    @PostMapping(path = "/register-url",produces = "application/json")
    public ResponseEntity<RegisterUrlResponse> registerUrl(){
        return ResponseEntity.ok(darajaApiService.registerUrl());
    }
    //validation end point
//    public ResponseEntity<> validateTransaction(){
//
//    }
}
