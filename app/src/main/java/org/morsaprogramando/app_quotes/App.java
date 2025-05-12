package org.morsaprogramando.app_quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

public class App implements RequestHandler<ScheduledEvent, Void> {

    @Override
    public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
        System.out.println("TODO");

        return null;
    }
}
