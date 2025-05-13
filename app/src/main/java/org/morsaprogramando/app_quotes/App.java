package org.morsaprogramando.app_quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.morsaprogramando.app_quotes.configuration.S3Config;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

public class App implements RequestHandler<ScheduledEvent, Void> {

    private final String bucketName = System.getenv("bucketName");


    @Override
    public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
        System.out.println("Initializing lambda");

        try(S3Client s3Client = new S3Config().create()) {
            setup(s3Client);
        }

        System.out.println("Finished");

        return null;
    }

    private void setup(S3Client s3Client) {
        readAndPrintObject(s3Client);
    }

    private void readAndPrintObject(S3Client s3Client) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key("test_file.json")
                .bucket(bucketName)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(objectRequest);

        try {
            response.transferTo(System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
