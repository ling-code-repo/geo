package com.qianyu.module.geo.component.article;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qianyu.module.geo.utils.GeoUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BgeEmbeddingModel implements EmbeddingModel {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${ai.embedding.url:''}")
    private String url;

    @Override
    public  Response<List<Embedding>> embedAll(List<TextSegment> texts) {

        try {
            List<String> list = texts.stream().map(TextSegment::text).toList();
            String body = mapper.writeValueAsString(
                    Map.of("texts", list)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            String body1 = response.body();
            JsonNode root = mapper.readTree(body1);

            List<Embedding> embeddings = new ArrayList<>();
            for (JsonNode vec : root.get("embeddings")) {
                float[] values = new float[vec.size()];
                int i = 0;
                for (JsonNode v : vec) {
                    values[i++] = (float) v.asDouble();
                }
                embeddings.add(Embedding.from(values));
            }
            return Response.from(embeddings);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TextSegment> getTextSegment(String question,String dir){
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(dir);
        if (documents.isEmpty()){
            return Collections.EMPTY_LIST;
        }
        Document document = documents.get(0);
        List<TextSegment> segments = DocumentSplitters.recursive(
                1000,  // 最大段落大小
                200    // 重叠大小，保持上下文
        ).split(document);
        Response<List<Embedding>> listResponse = embedAll(segments);

        EmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();
        List<Embedding> embeddingList = listResponse.content();

        for (int i = 0; i < embeddingList.size(); i++) {
            embeddingStore.add(embeddingList.get(i), segments.get(i));
        }

        Embedding questionEmbedding = embed(question).content();
        int maxResults = Math .min(GeoUtils.GENERATOR.nextInt(segments.size() - 1, segments.size() + 1), 5);
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(maxResults)       // Top-K
                .minScore(0.5)       // 相似度阈值（可选）
                .build();

        EmbeddingSearchResult<TextSegment> result =
                embeddingStore.search(request);
      return  result.matches().stream().map( EmbeddingMatch::embedded).collect(Collectors.toList());

    }

}
