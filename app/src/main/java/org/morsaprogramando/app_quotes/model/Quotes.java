package org.morsaprogramando.app_quotes.model;

import java.util.List;

public record Quotes(List<Quote> data) {

    public record Quote(Integer id, String text, Boolean read) {}

    public record Result(Quote quote, boolean exhausted) {}
}
