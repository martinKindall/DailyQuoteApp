package org.morsaprogramando.app_quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morsaprogramando.app_quotes.configuration.AWSClients;
import org.morsaprogramando.app_quotes.configuration.ObjectMapperConfig;
import org.morsaprogramando.app_quotes.model.Quotes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App implements RequestHandler<ScheduledEvent, Void> {

    private final String FILENAME = "test_file.json";
    private final String BUCKET_NAME = System.getenv("bucketName");


    @Override
    public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
        System.out.println("Initializing lambda");

        AWSClients clients = new AWSClients();
        ObjectMapper objectMapper = new ObjectMapperConfig().create();

        try(S3Client s3Client = clients.createS3Client();
            SesV2Client sesClient = clients.createSesClient()) {

            setup(s3Client, sesClient, objectMapper);
        }

        System.out.println("Finished");

        return null;
    }

    private void setup(S3Client s3Client, SesV2Client sesClient, ObjectMapper objectMapper) {
        Quotes quotes = getQuotes(s3Client, objectMapper);
        Quotes.Result result = Quotes.getRandom(quotes);

        sendEmail(sesClient, result.quote().text());

        updateQuotes(s3Client, quotes, result, objectMapper);
    }

    private Quotes getQuotes(S3Client s3Client, ObjectMapper objectMapper) {
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

    private void sendEmail(SesV2Client client, String text) {
        String subject = "Crecimiento personal";

        var emailRequest = SendEmailRequest.builder()
                .fromEmailAddressIdentityArn(System.getenv("SES_ARN"))
                .fromEmailAddress(System.getenv("EMAIL_ADDR"))
                .destination(Destination.builder()
                        .toAddresses(System.getenv("TO_ADDR"))
                        .build())
                .content(EmailContent.builder()
                        .simple(Message.builder()
                                .subject(Content.builder()
                                        .data(subject)
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

    private void updateQuotes(S3Client s3Client, Quotes quotes, Quotes.Result result, ObjectMapper objectMapper) {
        Quotes refreshedQuotes;

        if (result.exhausted()) {
            refreshedQuotes = new Quotes(
                    quotes.data().stream()
                            .map(quote -> new Quotes.Quote(
                                    quote.id(),
                                    quote.text(),
                                    false
                            ))
                            .toList()
            );
        } else {
            List<Quotes.Quote> filteredQuotes = new ArrayList<>(quotes.data().stream()
                    .filter(quote -> !Objects.equals(quote.id(), result.quote().id()))
                    .toList());

            filteredQuotes.add(new Quotes.Quote(
                    result.quote().id(),
                    result.quote().text(),
                    true
                    ));

            refreshedQuotes = new Quotes(filteredQuotes);
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .key(FILENAME)
                .bucket(BUCKET_NAME)
                .build();

        try {
            RequestBody body = RequestBody.fromString(
                    objectMapper.writeValueAsString(refreshedQuotes)
            );
            s3Client.putObject(request, body);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
