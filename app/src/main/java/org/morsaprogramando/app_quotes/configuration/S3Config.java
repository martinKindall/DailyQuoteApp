package org.morsaprogramando.app_quotes.configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Config {

    public S3Client create() {
        return S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }
}
