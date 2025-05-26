package org.morsaprogramando.app_quotes.service;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public class EmailService {

    private final SesV2Client client;

    public EmailService(SesV2Client client) {
        this.client = client;
    }

    public void sendEmail(String text) {
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
}
