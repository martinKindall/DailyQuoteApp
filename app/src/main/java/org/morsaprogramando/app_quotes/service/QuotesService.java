package org.morsaprogramando.app_quotes.service;

import org.morsaprogramando.app_quotes.model.Quotes;
import org.morsaprogramando.app_quotes.repository.QuotesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            return new Quotes.Result(random, true, quotes);
        }

        Quotes.Quote random = getRandom(quoteList);
        return new Quotes.Result(random, false, quotes);
    }

    public void updateQuotes(Quotes.Result result) {
        Quotes quotes = result.quotes();

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

        quotesRepository.updateQuotes(refreshedQuotes);
    }

    private static Quotes.Quote getRandom(List<Quotes.Quote> quoteList) {
        return quoteList.get(rand.nextInt(quoteList.size()));
    }

    private Quotes getQuotes() {
        return quotesRepository.getQuotes();
    }
}
