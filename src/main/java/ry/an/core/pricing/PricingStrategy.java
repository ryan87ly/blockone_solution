package ry.an.core.pricing;

import ry.an.model.order.CheckoutResult;

public interface PricingStrategy {
    CheckoutResult calculate(CheckoutResult checkoutResult);
}
