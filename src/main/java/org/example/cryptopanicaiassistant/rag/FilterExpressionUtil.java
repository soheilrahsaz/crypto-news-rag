package org.example.cryptopanicaiassistant.rag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.ai.vectorstore.filter.Filter;

import java.time.ZoneOffset;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterExpressionUtil {

    public static @NonNull Filter.Operand fromDateRange(@NonNull ModelDateRangeExtractor.DateRange dateRange) {
        Filter.Expression datetimeGreaterThanExpression = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("newsdatetime"), new Filter.Value(dateRange.startDate().atStartOfDay(ZoneOffset.UTC).toInstant()));
        Filter.Expression datetimeLessThanExpression = new Filter.Expression(Filter.ExpressionType.LTE, new Filter.Key("newsdatetime"), new Filter.Value(dateRange.endDate().atStartOfDay(ZoneOffset.UTC).toInstant()));
        return new Filter.Expression(Filter.ExpressionType.AND, datetimeGreaterThanExpression, datetimeLessThanExpression);
    }

    public static @NonNull Filter.Operand fromListOfCurrencies(@NonNull List<String> currencies) {
        if (currencies.isEmpty()) {
            throw new IllegalArgumentException("Currencies cannot be empty");
        }
        return new Filter.Expression(Filter.ExpressionType.IN, new Filter.Key("currencies"), new Filter.Value(currencies));
    }

    public static @NonNull Filter.Operand fromDateRangeAndCurrencies(@NonNull ModelDateRangeExtractor.DateRange dateRange, @NonNull List<String> currencies) {
        Filter.Operand dateRangeFilter = fromDateRange(dateRange);
        if(currencies.isEmpty()) {
            return dateRangeFilter;
        }

        Filter.Operand currenciesFilter = fromListOfCurrencies(currencies);
        return new Filter.Expression(Filter.ExpressionType.AND, dateRangeFilter, currenciesFilter);
    }
}
