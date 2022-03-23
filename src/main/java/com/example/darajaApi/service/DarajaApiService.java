package com.example.darajaApi.service;

import com.example.darajaApi.data.*;

public interface DarajaApiService {
    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    RegisterUrlResponse registerUrl();
    SimulateTransactionResponse simulateC2BTransaction(SimulateTransactionRequest simulateTransactionRequest);
    CommonSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest);
    TransactionStatusSyncResponse getTransactionResult(InternalTransactionStatusRequest internalTransactionStatusRequest);
    CommonSyncResponse checkAccountBalance();

    StkPushSyncResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest);

    LNMQueryResponse getTransactionStatus(InternalLNMRequest internalLNMRequest);


}
