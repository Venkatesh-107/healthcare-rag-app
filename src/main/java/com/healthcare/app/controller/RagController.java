package com.healthcare.app.controller;

import com.healthcare.app.service.RagService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body("Query missing");
        }
        String answer = ragService.chat(query);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // Save to temp file to load as Resource (LangChain4j currently prefers Paths or
            // Resources)
            Path tempDir = Files.createTempDirectory("rag_uploads");
            Path tempFile = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(tempFile);

            ragService.ingest(new FileSystemResource(tempFile.toFile()));

            return ResponseEntity.ok("Document ingested successfully!");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to ingest document: " + e.getMessage());
        }
    }
}
