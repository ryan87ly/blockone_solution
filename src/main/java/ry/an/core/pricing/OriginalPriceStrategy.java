package ry.an.core.pricing;

import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;
import ry.an.model.product.Product;

import java.util.List;
import java.util.stream.Collectors;

public class OriginalPriceStrategy extends PricingStrategy {
    public static OriginalPriceStrategy INSTANCE = new OriginalPriceStrategy();

    private OriginalPriceStrategy() {

    }

    @Override
    protected PricingStrategyProcessResult doCalculate(CheckoutResult checkoutResult) {
        List<CheckoutItemResult> proceededItems = checkoutResult.getRemainingItems().getItems().stream()
                .filter(checkoutItem -> checkoutItem.getQuantity() > 0)
                .map(checkoutItem -> {
                    Product product = checkoutItem.getProduct();
                    Price itemPrice = product.getPrice().multiply(checkoutItem.getQuantity());
                    return CheckoutItemResult.of(product, checkoutItem.getQuantity(), itemPrice, itemPrice, false);
                })
                .collect(Collectors.toList());
        List<CheckoutItem> updatedRemainingItems = checkoutResult.getRemainingItems().getItems().stream()
                .filter(checkoutItem -> checkoutItem.getQuantity() > 0)
                .map(checkoutItem -> CheckoutItem.of(checkoutItem.getProduct(), 0))
                .collect(Collectors.toList());
        return PricingStrategyProcessResult.of(proceededItems, updatedRemainingItems);
    }
}
