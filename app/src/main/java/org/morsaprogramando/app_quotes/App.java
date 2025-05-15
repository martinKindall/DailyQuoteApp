package org.morsaprogramando.app_quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morsaprogramando.app_quotes.configuration.AWSClients;
import org.morsaprogramando.app_quotes.configuration.ObjectMapperConfig;
import org.morsaprogramando.app_quotes.model.Quotes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class App implements RequestHandler<ScheduledEvent, Void> {

    private final String bucketName = System.getenv("bucketName");


    @Override
    public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
        System.out.println("Initializing lambda");

        AWSClients clients = new AWSClients();
        ObjectMapperConfig mapperConfig = new ObjectMapperConfig();

        try(S3Client s3Client = clients.createS3Client();
            SesV2Client sesClient = clients.createSesClient()) {

            setup(s3Client, sesClient, mapperConfig.create());
        }

        System.out.println("Finished");

        return null;
    }

    private void setup(S3Client s3Client, SesV2Client sesClient, ObjectMapper objectMapper) {
        Quotes quotes = getQuotes(s3Client, objectMapper);
        Quotes.Result result = Quotes.getRandom(quotes);

        sendEmail(sesClient, result.quote());

        updateDatabase(s3Client, quotes, result);
    }

    private Quotes getQuotes(S3Client s3Client, ObjectMapper objectMapper) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key("test_file.json")
                .bucket(bucketName)
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

    private void sendEmail(SesV2Client client, String text) {
        var emailRequest = SendEmailRequest.builder()
                .fromEmailAddressIdentityArn(System.getenv("SES_ARN"))
                .fromEmailAddress(System.getenv("EMAIL_ADDR"))
                .destination(Destination.builder()
                        .toAddresses(System.getenv("TO_ADDR"))
                        .build())
                .content(EmailContent.builder()
                        .simple(Message.builder()
                                .subject(Content.builder()
                                        .data("Crecimiento personal")
                                        .build())
                                .body(Body.builder()
                                        .text(Content.builder()
                                                .data(text)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        client.sendEmail(emailRequest);
    }

    private void updateDatabase(S3Client s3Client, Quotes quotes, Quotes.Result result) {

    }
}
