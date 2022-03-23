package com.example.darajaApi.service;

import com.example.darajaApi.config.MpesaConfiguration;
import com.example.darajaApi.data.AccessTokenResponse;
import com.example.darajaApi.data.RegisterUrlRequest;
import com.example.darajaApi.data.RegisterUrlResponse;
import com.example.darajaApi.utility.HelperUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.example.darajaApi.utility.Constants.*;
@Service
@Slf4j

public class DarajaApiServiceImplementation  implements DarajaApiService{

    private final MpesaConfiguration mpesaConfiguration;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    public DarajaApiServiceImplementation(MpesaConfiguration mpesaConfiguration, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        this.mpesaConfiguration = mpesaConfiguration;
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public AccessTokenResponse getAccessToken() {
        // get the Base64 rep of consumerKey + ":" + consumerSecret

        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(),
                mpesaConfiguration.getConsumerSecret()));

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Override
    public RegisterUrlResponse registerUrl() {
        AccessTokenResponse accessTokenResponse=getAccessToken();
        RegisterUrlRequest registerUrlRequest=new RegisterUrlRequest();
        registerUrlRequest.setConfirmationURL(mpesaConfiguration.getConfirmationURL());
        registerUrlRequest.setResponseType(mpesaConfiguration.getResponseType());
        registerUrlRequest.setShortCode(mpesaConfiguration.getShortCode());
        registerUrlRequest.setValidationURL(mpesaConfiguration.getValidationURL());

        RequestBody body=RequestBody.create(JSON_MEDIA_TYPE, Objects.requireNonNull(HelperUtility.toJson(registerUrlRequest)));

        Request request= new Request.Builder()
                .url(mpesaConfiguration.getRegisterUrlEndpoint())
                .post(body)
                .addHeader("Authorization", String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response=okHttpClient.newCall(request).execute();

            assert response.body() !=null;
            return  objectMapper.readValue(response.body().string(),RegisterUrlResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }


    }


}

