package ry.an.core.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ry.an.service.product.ProductService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ry.an.util.DiscountsForTesting.*;

class DiscountStrategyFactoryImplTest {

    @Mock
    private ProductService productService;

    private DiscountStrategyFactoryImpl discountStrategyFactory;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        discountStrategyFactory = DiscountStrategyFactoryImpl.of(productService);
    }

    @Test
    void testCreateBundleProductsDiscountStrategy() {
        assertTrue(discountStrategyFactory.createStrategy(bundleDiscount1) instanceof BundleProductsDiscountStrategy);
        assertTrue(discountStrategyFactory.createStrategy(bundleDiscount2) instanceof BundleProductsDiscountStrategy);
    }

    @Test
    void testCreatePricePercentReductionDiscountStrategy() {
        assertTrue(discountStrategyFactory.createStrategy(pricingDiscount1) instanceof PricePercentageDiscountStrategy);
        assertTrue(discountStrategyFactory.createStrategy(pricingDiscount2) instanceof PricePercentageDiscountStrategy);
    }
}