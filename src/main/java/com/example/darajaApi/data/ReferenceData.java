package com.example.darajaApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReferenceData {
    @JsonProperty("ReferenceItem")
    private ReferenceItem referenceItem;
}
