package org.morsaprogramando.app_quotes.configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sesv2.SesV2Client;

public class AWSClients {

    private final Region region = Region.EU_CENTRAL_1;

    public S3Client createS3Client() {
        return S3Client.builder()
                .region(region)
                .build();
    }

    public SesV2Client createSesClient() {
        return SesV2Client.builder()
                .region(region)
                .build();
    }
}
