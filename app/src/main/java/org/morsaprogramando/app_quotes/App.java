package org.morsaprogramando.app_quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morsaprogramando.app_quotes.configuration.AWSClients;
import org.morsaprogramando.app_quotes.configuration.ObjectMapperConfig;
import org.morsaprogramando.app_quotes.model.Quotes;
import org.morsaprogramando.app_quotes.repository.QuotesRepository;
import org.morsaprogramando.app_quotes.service.EmailService;
import org.morsaprogramando.app_quotes.service.QuotesService;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sesv2.SesV2Client;


public class App implements RequestHandler<ScheduledEvent, Void> {

    @Override
    public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
        System.out.println("Initializing lambda");

        AWSClients clients = new AWSClients();
        ObjectMapper objectMapper = new ObjectMapperConfig().create();

        try(S3Client s3Client = clients.createS3Client();
            SesV2Client sesClient = clients.createSesClient()) {

            QuotesRepository quotesRepository = new QuotesRepository(s3Client, objectMapper);
            QuotesService quotesService = new QuotesService(quotesRepository);
            EmailService emailService = new EmailService(sesClient);

            setup(quotesService, emailService);
        }

        System.out.println("Finished");

        return null;
    }

    private void setup(QuotesService quotesService, EmailService emailService) {
        Quotes.Result result = quotesService.getRandom();

        emailService.sendEmail(result.quote().text());

        quotesService.updateQuotes(result);
    }
}
