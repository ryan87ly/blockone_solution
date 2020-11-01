package ry.an.core.pricing;

import ry.an.model.discount.DiscountEntity;

public interface DiscountStrategyFactory {
    PricingStrategy createStrategy(DiscountEntity discountEntity);
}
