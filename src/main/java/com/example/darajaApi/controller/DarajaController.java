package com.example.darajaApi.controller;

import com.example.darajaApi.data.*;
import com.example.darajaApi.repository.B2CC2BEntriesRepository;
import com.example.darajaApi.repository.StkPushEntriesRepository;
import com.example.darajaApi.service.DarajaApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import documents.B2C_C2B_Entries;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("mobile-money")
@Slf4j
public class DarajaController {

    private final DarajaApiService darajaApiService;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;
    private final StkPushEntriesRepository stkPushEntriesRepository;
    private final B2CC2BEntriesRepository b2CC2BEntriesRepository;


    public DarajaController(DarajaApiService darajaApiService, AcknowledgeResponse acknowledgeResponse, ObjectMapper objectMapper, StkPushEntriesRepository stkPushEntriesRepository, B2CC2BEntriesRepository b2CC2BEntriesRepository) {
        this.darajaApiService = darajaApiService;
        this.acknowledgeResponse = acknowledgeResponse;
        this.objectMapper = objectMapper;
        this.stkPushEntriesRepository = stkPushEntriesRepository;
        this.b2CC2BEntriesRepository = b2CC2BEntriesRepository;
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
    @PostMapping(path = "/simulate-c2b", produces = "application/json")
    public ResponseEntity<SimulateTransactionResponse> simulateB2CTransaction(@RequestBody SimulateTransactionRequest simulateTransactionRequest) {

        SimulateTransactionResponse simulateTransactionResponse = darajaApiService.simulateC2BTransaction(simulateTransactionRequest);

        B2C_C2B_Entries b2C_c2BEntry = new B2C_C2B_Entries();
        b2C_c2BEntry.setTransactionType("C2B");
        b2C_c2BEntry.setBillRefNumber(simulateTransactionRequest.getBillRefNumber());
        b2C_c2BEntry.setAmount(simulateTransactionRequest.getAmount());
        b2C_c2BEntry.setEntryDate(new Date());
        b2C_c2BEntry.setOriginatorConversationId(simulateTransactionResponse.getOriginatorCoversationID());
        b2C_c2BEntry.setConversationId(simulateTransactionResponse.getConversationID());
        b2C_c2BEntry.setMsisdn(simulateTransactionRequest.getMsisdn());

        b2CC2BEntriesRepository.save(b2C_c2BEntry);

        return ResponseEntity.ok(simulateTransactionResponse);
    }


    @PostMapping(path = "/transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> b2cTransactionAsyncResults(@RequestBody B2CTransactionAsyncResponse b2CTransactionAsyncResponse)
            throws JsonProcessingException {
        log.info("============ Transaction Result =============");
        log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));

        Result b2cResult = b2CTransactionAsyncResponse.getResult();

        B2C_C2B_Entries b2cInternalRecord = b2CC2BEntriesRepository.findByConversationIdOrOriginatorConversationId(
                b2cResult.getConversationID(),
                b2cResult.getOriginatorConversationID());

        b2cInternalRecord.setRawCallbackPayloadResponse(b2CTransactionAsyncResponse);
        b2cInternalRecord.setResultCode(String.valueOf(b2cResult.getResultCode()));
        b2cInternalRecord.setTransactionId(b2cResult.getTransactionID());

        b2CC2BEntriesRepository.save(b2cInternalRecord);

        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-queue-timeout", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> queueTimeout(@RequestBody Object object) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-transaction", produces = "application/json")
    public ResponseEntity<CommonSyncResponse> performB2CTransaction(@RequestBody InternalB2CTransactionRequest internalB2CTransactionRequest) {
        CommonSyncResponse commonSyncResponse = darajaApiService.performB2CTransaction(internalB2CTransactionRequest);

        B2C_C2B_Entries b2C_c2BEntry = new B2C_C2B_Entries();
        b2C_c2BEntry.setTransactionType("B2C");
        b2C_c2BEntry.setAmount(internalB2CTransactionRequest.getAmount());
        b2C_c2BEntry.setEntryDate(new Date());
        b2C_c2BEntry.setOriginatorConversationId(commonSyncResponse.getOriginatorConversationID());
        b2C_c2BEntry.setConversationId(commonSyncResponse.getConversationID());
        b2C_c2BEntry.setMsisdn(internalB2CTransactionRequest.getPartyB());

        b2CC2BEntriesRepository.save(b2C_c2BEntry);

        return ResponseEntity.ok(commonSyncResponse);
    }

    @PostMapping(path = "/simulate-transaction-result", produces = "application/json")
    public ResponseEntity<TransactionStatusSyncResponse> getTransactionStatusResult(@RequestBody InternalTransactionStatusRequest internalTransactionStatusRequest) {
        return ResponseEntity.ok(darajaApiService.getTransactionResult(internalTransactionStatusRequest));
    }

    @GetMapping(path = "/check-account-balance", produces = "application/json")
    public ResponseEntity<CommonSyncResponse> checkAccountBalance() {
        return ResponseEntity.ok(darajaApiService.checkAccountBalance());
    }

    @PostMapping(path = "/stk-transaction-request", produces = "application/json")
    public ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest) {
        return ResponseEntity.ok(darajaApiService.performStkPushTransaction(internalStkPushRequest));
    }

    @SneakyThrows
    @PostMapping(path = "/stk-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> acknowledgeStkPushResponse(@RequestBody StkPushAsyncResponse stkPushAsyncResponse) {
        log.info("======= STK Push Async Response =====");
        log.info(objectMapper.writeValueAsString(stkPushAsyncResponse));
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/query-lnm-request", produces = "application/json")
    public ResponseEntity<LNMQueryResponse> getTransactionStatus(@RequestBody InternalLNMRequest internalLNMRequest) {
        return ResponseEntity.ok(darajaApiService.getTransactionStatus(internalLNMRequest));
    }
}
