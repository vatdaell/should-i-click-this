package com.ansaf.shouldiclickthis.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SuccessResponse {
    private String domain;
    private boolean status;
    private LocalDateTime responseTime;
}
