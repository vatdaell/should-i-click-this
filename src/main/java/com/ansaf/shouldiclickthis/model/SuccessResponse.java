package com.ansaf.shouldiclickthis.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Data
@Builder
public class SuccessResponse {
    private String url;
    private boolean status;
    private String responseTime;
    private String lastUpdated;
}
