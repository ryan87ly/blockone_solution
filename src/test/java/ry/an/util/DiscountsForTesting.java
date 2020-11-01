package ry.an.util;

import ry.an.model.discount.BundleProductsDiscount;
import ry.an.model.discount.DiscountRate;
import ry.an.model.discount.PricePercentageDiscount;

import java.math.BigDecimal;
import java.util.List;

import static ry.an.util.ProductsForTesting.*;

public final class DiscountsForTesting {
    public static final BundleProductsDiscount bundleDiscount1 = BundleProductsDiscount.of("Discount-1", "Buy product 1 get product2 for free", true, testProduct1.getId(), List.of(testProduct2.getId()));
    public static final BundleProductsDiscount bundleDiscount2 = BundleProductsDiscount.of("Discount-2", "Buy product 1 get product2 and product 3for free", true, testProduct1.getId(), List.of(testProduct2.getId(), testProduct3.getId()));
    public static final BundleProductsDiscount bundleDiscount3 = BundleProductsDiscount.of("Discount-3", "Buy product 2 get product3 for free", true, testProduct2.getId(), List.of(testProduct3.getId()));

    public static final PricePercentageDiscount pricingDiscount1 = PricePercentageDiscount.of("D-1", "10% off on product1", true, testProduct1.getId(), 1, DiscountRate.fromPercentage(10));
    public static final PricePercentageDiscount pricingDiscount2 = PricePercentageDiscount.of("D-2", "50% off on second product1", true, testProduct2.getId(), 2, DiscountRate.fromPercentage(50));

}
