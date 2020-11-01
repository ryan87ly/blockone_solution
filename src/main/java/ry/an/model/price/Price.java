package ry.an.model.price;

import ry.an.model.discount.DiscountRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Price {
    public static Price ZERO = fromDollar(BigDecimal.ZERO);

    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final BigDecimal priceValue;

    private Price(BigDecimal underlying) {
        this.priceValue = underlying;
    }

    public static Price fromDollar(BigDecimal priceValue) {
        return new Price(priceValue);
    }

    public static Price fromDollar(long val) {
        return new Price(BigDecimal.valueOf(val));
    }

    public static Price fromCents(long cents) {
        return Price.fromDollar(BigDecimal.valueOf(cents, 2));
    }

    public Price add(Price other) {
        return fromDollar(priceValue.add(other.priceValue));
    }

    public Price multiply(int other) {
        return fromDollar(priceValue.multiply(BigDecimal.valueOf(other)));
    }

    public Price multiply(DiscountRate discountRate) {
        return fromDollar(priceValue.multiply(discountRate.getRateValue()));
    }

    public Price subtract(Price other) {
        return fromDollar(priceValue.subtract(other.priceValue));
    }

    public long getValueInCents() {
        return priceValue.setScale(2, RoundingMode.UP).multiply(ONE_HUNDRED).longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return priceValue.compareTo(price.priceValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priceValue);
    }

    @Override
    public String toString() {
        return "Price{" +
                "priceValue=" + priceValue +
                '}';
    }
}
