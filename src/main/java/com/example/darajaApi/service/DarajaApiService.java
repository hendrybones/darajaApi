package com.example.darajaApi.service;

import com.example.darajaApi.data.AccessTokenResponse;
import com.example.darajaApi.data.RegisterUrlResponse;

public interface DarajaApiService {
    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    RegisterUrlResponse registerUrl();
}
