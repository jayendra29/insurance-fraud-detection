package com.insurance.backend.document.repository;

import com.insurance.backend.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByClaimIdOrderByUploadedAtDesc(Long claimId);
}