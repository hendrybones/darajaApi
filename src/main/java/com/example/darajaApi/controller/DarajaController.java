package com.example.darajaApi.controller;

import com.example.darajaApi.data.AccessTokenResponse;
import com.example.darajaApi.data.AcknowledgeResponse;
import com.example.darajaApi.data.MpesaValidationResponse;
import com.example.darajaApi.data.RegisterUrlResponse;
import com.example.darajaApi.service.DarajaApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("mobile-money")
public class DarajaController {

    private final DarajaApiService darajaApiService;
    private final AcknowledgeResponse acknowledgeResponse;

    public DarajaController(DarajaApiService darajaApiService, AcknowledgeResponse acknowledgeResponse) {
        this.darajaApiService = darajaApiService;
        this.acknowledgeResponse = acknowledgeResponse;
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
    @PostMapping(path = "/validation", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> mpesaValidation(@RequestBody MpesaValidationResponse mpesaValidationResponse) {

        B2C_C2B_Entries b2CC2BEntry = b2CC2BEntriesRepository.findByBillRefNumber(mpesaValidationResponse.getBillRefNumber());

        b2CC2BEntry.setRawCallbackPayloadResponse(mpesaValidationResponse);
        b2CC2BEntry.setResultCode("0");
        b2CC2BEntry.setTransactionId(mpesaValidationResponse.getTransID());

        b2CC2BEntriesRepository.save(b2CC2BEntry);

        return ResponseEntity.ok(acknowledgeResponse);
    }
}
