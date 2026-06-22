package com.insurance.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String message;
}
