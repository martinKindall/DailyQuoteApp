package org.morsaprogramando.app_quotes.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.morsaprogramando.app_quotes.model.Quotes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class QuotesRepository {

    private final String FILENAME = "test_file.json";
    private final String BUCKET_NAME = System.getenv("bucketName");

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    public QuotesRepository(S3Client s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }

    public Quotes getQuotes() {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(FILENAME)
                .bucket(BUCKET_NAME)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(objectRequest);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.transferTo(outputStream);
            String jsonContent = outputStream.toString(StandardCharsets.UTF_8);

            return objectMapper.readValue(jsonContent, Quotes.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
