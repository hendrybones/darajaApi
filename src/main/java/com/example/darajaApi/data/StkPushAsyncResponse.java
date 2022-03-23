package com.example.darajaApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.StreamingHttpOutputMessage;

@Data
public class StkPushAsyncResponse {
    @JsonProperty("Body")
    private StreamingHttpOutputMessage.Body body;
}
