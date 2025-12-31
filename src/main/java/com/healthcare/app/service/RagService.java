package com.healthcare.app.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.data.document.splitter.DocumentSplitters;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ContentRetriever contentRetriever;

    public RagService() {
        // Initialize local embedding model (no API key required)
        this.embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        this.contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3) // Return top 3 results
                .minScore(0.5)
                .build();
    }

    // Ingests a Spring Resource (e.g. from file upload or classpath)
    public void ingest(Resource resource) throws IOException {
        Document document = dev.langchain4j.data.document.loader.FileSystemDocumentLoader
                .loadDocument(resource.getFile().toPath(), new TextDocumentParser());
        var ingestor = dev.langchain4j.store.embedding.EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);
    }

    public String chat(String queryText) {
        Query query = Query.from(queryText);
        List<Content> contents = contentRetriever.retrieve(query);

        if (contents.isEmpty()) {
            return "No relevant information found in the documents.";
        }

        return contents.stream()
                .map(Content::textSegment)
                .map(TextSegment::text)
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
