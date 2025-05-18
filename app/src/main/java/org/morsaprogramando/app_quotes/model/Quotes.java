package org.morsaprogramando.app_quotes.model;

import java.util.List;
import java.util.Random;

public record Quotes(List<Quote> data) {

    private static final Random rand = new Random();


    public record Quote(Integer id, String text, Boolean read) {}

    public static Result getRandom(Quotes quotes) {
        List<Quote> quoteList = quotes.data().stream()
                .filter(element -> !element.read())
                .toList();

        if (quoteList.isEmpty()) {
            Quote random = getRandom(quotes.data);
            return new Result(random, true);
        }

        Quote random = getRandom(quoteList);
        return new Result(random, false);
    }

    private static Quote getRandom(List<Quote> quoteList) {
        return quoteList.get(rand.nextInt(quoteList.size()));
    }

    public record Result(Quote quote, boolean exhausted) {}
}
