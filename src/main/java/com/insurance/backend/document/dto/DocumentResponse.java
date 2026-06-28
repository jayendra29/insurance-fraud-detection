package com.insurance.backend.document.dto;

import com.insurance.backend.document.entity.Document;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String originalFileName,
        String fileType,
        Long fileSize,
        LocalDateTime uploadedAt,
        Long claimId
) {

    public static DocumentResponse fromEntity(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getOriginalFileName(),
                document.getFileType(),
                document.getFileSize(),
                document.getUploadedAt(),
                document.getClaim().getId()
        );
    }
}