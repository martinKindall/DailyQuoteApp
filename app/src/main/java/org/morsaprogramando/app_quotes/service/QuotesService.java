package org.morsaprogramando.app_quotes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.morsaprogramando.app_quotes.model.Quotes;
import org.morsaprogramando.app_quotes.repository.QuotesRepository;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Random;

public class QuotesService {

    private static final Random rand = new Random();

    private final QuotesRepository quotesRepository;

    public QuotesService(QuotesRepository quotesRepository) {
        this.quotesRepository = quotesRepository;
    }

    public Quotes.Result getRandom() {
        Quotes quotes = getQuotes();

        List<Quotes.Quote> quoteList = quotes.data().stream()
                .filter(element -> !element.read())
                .toList();

        if (quoteList.isEmpty()) {
            Quotes.Quote random = getRandom(quotes.data());
            return new Quotes.Result(random, true);
        }

        Quotes.Quote random = getRandom(quoteList);
        return new Quotes.Result(random, false);
    }

    private static Quotes.Quote getRandom(List<Quotes.Quote> quoteList) {
        return quoteList.get(rand.nextInt(quoteList.size()));
    }

    private Quotes getQuotes() {
        return quotesRepository.getQuotes();
    }
}
