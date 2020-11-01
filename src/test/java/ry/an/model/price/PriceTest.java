package ry.an.model.price;

import org.junit.jupiter.api.Test;
import ry.an.model.discount.DiscountRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    @Test
    void testCreateFromCents() {
        assertEquals(Price.fromDollar(BigDecimal.valueOf(2.05)), Price.fromCents(205));
        assertEquals(Price.fromDollar(BigDecimal.valueOf(10.0)), Price.fromCents(1000));
        assertEquals(Price.ZERO, Price.fromCents(0));
    }

    @Test
    void testAdd() {
        assertEquals(Price.fromCents(450), Price.fromCents(100).add(Price.fromCents(350)));
        assertEquals(Price.fromCents(12000), Price.fromCents(10000).add(Price.fromCents(2000)));
        assertEquals(Price.fromDollar(BigDecimal.valueOf(200)), Price.fromCents(10000).add(Price.fromDollar(100)));
    }

    @Test
    void testSubtract() {
        assertEquals(Price.fromCents(250), Price.fromCents(350).subtract(Price.fromCents(100)));
        assertEquals(Price.fromCents(8000), Price.fromCents(10000).subtract(Price.fromCents(2000)));
        assertEquals(Price.fromDollar(BigDecimal.valueOf(10)), Price.fromCents(11000).subtract(Price.fromDollar(100)));
    }

    @Test
    void testMultiply() {
        assertEquals(Price.fromDollar(BigDecimal.valueOf(20.0475)), Price.fromCents(2025).multiply(DiscountRate.fromPercentage(99)));
        assertEquals(Price.fromDollar(BigDecimal.valueOf(5.0)), Price.fromCents(5000).multiply(DiscountRate.fromPercentage(10)));
        assertEquals(Price.fromDollar(BigDecimal.valueOf(5000)), Price.fromDollar(50).multiply(100));
    }

    @Test
    void testGetValueInCents() {
        assertEquals(2005, Price.fromCents(2025).multiply(DiscountRate.fromPercentage(99)).getValueInCents());
        assertEquals(3334, Price.fromDollar(BigDecimal.valueOf(100).setScale(5).divide(BigDecimal.valueOf(3), RoundingMode.UP)).getValueInCents());
    }

}