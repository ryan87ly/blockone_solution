package ry.an.model.discount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DiscountRateTest {

    @Test
    void testCreateFromPercentage() {
        assertEquals(0, DiscountRate.fromPercentage(99).getRateValue().compareTo(BigDecimal.valueOf(0.99)));
        assertEquals(0, DiscountRate.fromPercentage(10).getRateValue().compareTo(BigDecimal.valueOf(0.1)));
        assertEquals(0, DiscountRate.fromPercentage(90).getRateValue().compareTo(BigDecimal.valueOf(0.9)));
    }

}