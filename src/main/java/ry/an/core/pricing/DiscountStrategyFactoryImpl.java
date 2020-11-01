package ry.an.core.pricing;

import org.springframework.stereotype.Component;
import ry.an.model.discount.BundleProductsDiscount;
import ry.an.model.discount.DiscountEntity;
import ry.an.model.discount.PricePercentageDiscount;
import ry.an.service.product.ProductService;

@Component
public class DiscountStrategyFactoryImpl implements DiscountStrategyFactory {
    private final ProductService productService;

    private DiscountStrategyFactoryImpl(ProductService productService) {
        this.productService = productService;
    }

    public static DiscountStrategyFactoryImpl of(ProductService productService) {
        return new DiscountStrategyFactoryImpl(productService);
    }

    @Override
    public PricingStrategy createStrategy(DiscountEntity discountEntity) {
        switch (discountEntity.getProductDiscountType()) {
            case BUNDLE_PRODUCTS:
                return BundleProductsDiscountStrategy.of((BundleProductsDiscount) discountEntity, productService);
            case PRICE_PERCENTAGE_REDUCTION:
                return PricePercentageDiscountStrategy.of((PricePercentageDiscount) discountEntity);
            default:
                throw new IllegalArgumentException(String.format("Supported %s", discountEntity.getProductDiscountType()));
        }
    }
}
