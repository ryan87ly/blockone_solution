package ry.an.model.discount;

import java.math.BigDecimal;

public final class DiscountRate {
    private final BigDecimal rateValue;

    private DiscountRate(BigDecimal rateValue) {
        this.rateValue = rateValue;
    }

    public static DiscountRate fromPercentage(int percentageValue) {
        return new DiscountRate(BigDecimal.valueOf(percentageValue, 2));
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public int getPercentageValue() {
        return rateValue.unscaledValue().intValue();
    }
}
