package com.ansaf.shouldiclickthis.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Data
@Builder
public class SuccessResponse {
    private String link;
    private String domain;
    private boolean status;
    private LocalDateTime responseTime;
}
