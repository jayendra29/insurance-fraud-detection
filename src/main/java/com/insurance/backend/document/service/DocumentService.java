package com.insurance.backend.document.service;

import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.repository.ClaimRepository;
import com.insurance.backend.document.dto.DocumentResponse;
import com.insurance.backend.document.entity.Document;
import com.insurance.backend.document.repository.DocumentRepository;
import com.insurance.backend.exception.FileStorageException;
import com.insurance.backend.exception.InvalidFileException;
import com.insurance.backend.exception.ResourceNotFoundException;
import com.insurance.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;

    private static final String UPLOAD_DIRECTORY = "uploads";

    private final DocumentRepository documentRepository;
    private final ClaimRepository claimRepository;

    @Transactional
    public DocumentResponse uploadDocument(Long claimId, MultipartFile file) {
        Claim claim = findClaimOwnedByCurrentUser(claimId);
        validateFile(file);

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + getFileExtension(originalFileName);

        Path targetPath = resolveUploadDirectory().resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file: " + originalFileName, ex);
        }

        Document document = Document.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .claim(claim)
                .build();

        Document savedDocument = documentRepository.save(document);
        return DocumentResponse.fromEntity(savedDocument);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsForClaim(Long claimId) {
        findClaimOwnedByCurrentUser(claimId);
        return documentRepository.findByClaimIdOrderByUploadedAtDesc(claimId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        Document document = findDocumentOwnedByCurrentUser(documentId);
        Path filePath = resolveUploadDirectory().resolve(document.getStoredFileName());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found on disk: " + document.getOriginalFileName());
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Failed to read file: " + document.getOriginalFileName(), ex);
        }
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = findDocumentOwnedByCurrentUser(documentId);
        Path filePath = resolveUploadDirectory().resolve(document.getStoredFileName());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to delete file: " + document.getOriginalFileName(), ex);
        }

        documentRepository.delete(document);
    }

    public String getDocumentFileName(Long documentId) {
        return findDocumentOwnedByCurrentUser(documentId).getOriginalFileName();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Uploaded file must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new InvalidFileException(
                    "Unsupported file type: " + contentType + ". Allowed types are JPEG, PNG and PDF");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileException("File size exceeds the maximum allowed limit of 10 MB");
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidFileException("Uploaded file must have a valid name");
        }
        return Paths.get(fileName).getFileName().toString();
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex);
    }

    private Path resolveUploadDirectory() {
        Path uploadPath = Paths.get(UPLOAD_DIRECTORY).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to initialize upload directory", ex);
        }
        return uploadPath;
    }

    private Claim findClaimOwnedByCurrentUser(Long claimId) {
        User currentUser = getAuthenticatedUser();

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (!claim.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this claim");
        }

        return claim;
    }

    private Document findDocumentOwnedByCurrentUser(Long documentId) {
        User currentUser = getAuthenticatedUser();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        if (!document.getClaim().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this document");
        }

        return document;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}