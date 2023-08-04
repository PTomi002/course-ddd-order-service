package hu.paulintamas.foodorderingsystem.domain.valueobject;

import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Value(staticConstructor = "of")
public class Money {
    public static final Money ZERO = Money.of(BigDecimal.ZERO);

    @NonNull
    BigDecimal amount;

    public boolean isGreaterThan(final Money money) {
        return amount.compareTo(money.getAmount()) > 0;
    }

    public boolean isGreaterThanZero() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public Money add(final Money money) {
        return Money.of(setScale(amount.add(money.getAmount())));
    }

    public Money subtract(final Money money) {
        return Money.of(setScale(amount.subtract(money.getAmount())));
    }

    public Money multiply(final int multiplier) {
        return Money.of(setScale(amount.multiply(BigDecimal.valueOf(multiplier))));
    }

    private BigDecimal setScale(final BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }

}
