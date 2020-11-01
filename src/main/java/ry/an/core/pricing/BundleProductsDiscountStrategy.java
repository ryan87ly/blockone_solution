package ry.an.core.pricing;

import ry.an.exception.ProductNotFoundException;
import ry.an.model.discount.BundleProductsDiscount;
import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;
import ry.an.model.product.Product;
import ry.an.service.product.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BundleProductsDiscountStrategy extends PricingStrategy {
    private final BundleProductsDiscount discountDefinition;
    private final ProductService productService;

    private BundleProductsDiscountStrategy(BundleProductsDiscount discountDefinition, ProductService productService) {
        this.discountDefinition = discountDefinition;
        this.productService = productService;
    }

    public static BundleProductsDiscountStrategy of(BundleProductsDiscount discountDefinition, ProductService productService) {
        return new BundleProductsDiscountStrategy(discountDefinition, productService);
    }

    @Override
    protected PricingStrategyProcessResult doCalculate(CheckoutResult checkoutResult) {
        String discountedProductId = discountDefinition.getProductId();

        // If the remaining items don't contain discounted product, skip
        CheckoutItem checkoutItem = checkoutResult.getRemainingItems().getItemByProductId(discountedProductId);
        if (Objects.isNull(checkoutItem)) {
            return PricingStrategyProcessResult.EMPTY;
        }
        List<CheckoutItemResult> proceededItems = new ArrayList<>();
        List<Product> freeProducts = discountDefinition.getFreeProductIds()
            .stream()
            .map(productId -> productService.
                findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId))
            )
            .collect(Collectors.toList());

        Product checkoutProduct = checkoutItem.getProduct();
        Price itemPrice = checkoutProduct.getPrice().multiply(checkoutItem.getQuantity());
        CheckoutItemResult itemResult = CheckoutItemResult.of(checkoutProduct, checkoutItem.getQuantity(), itemPrice, itemPrice, false);
        proceededItems.add(itemResult);

        for (Product freeProduct : freeProducts) {
            Price originalPrice = freeProduct.getPrice().multiply(checkoutItem.getQuantity());
            CheckoutItemResult freeItemResult = CheckoutItemResult.of(freeProduct, checkoutItem.getQuantity(), originalPrice, Price.ZERO, true);
            proceededItems.add(freeItemResult);
        }
        CheckoutItem remainingCheckoutItem = CheckoutItem.of(checkoutProduct, 0);
        return PricingStrategyProcessResult.of(proceededItems, List.of(remainingCheckoutItem));
    }
}
