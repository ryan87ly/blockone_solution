package ry.an.core.pricing;

import ry.an.model.product.Product;
import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItems;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OriginalPriceStrategy implements PricingStrategy {
    public static OriginalPriceStrategy INSTANCE = new OriginalPriceStrategy();

    private OriginalPriceStrategy() {

    }

    @Override
    public CheckoutResult calculate(CheckoutResult checkoutResult) {
        List<CheckoutItemResult> updatedProceededItems = new ArrayList<>(checkoutResult.getProcessedItems());
        for (CheckoutItem checkoutItem : checkoutResult.getRemainingItems().getItems()) {
            int quantity = checkoutItem.getQuantity();
            if (quantity <= 0) {
                continue;
            }
            Product product = checkoutItem.getProduct();
            Price itemPrice = product.getPrice().multiply(quantity);
            CheckoutItemResult itemResult = CheckoutItemResult.of(product, quantity, itemPrice, itemPrice, false);
            updatedProceededItems.add(itemResult);
        }
        return CheckoutResult.of(updatedProceededItems, CheckoutItems.of(Collections.emptyMap()));
    }
}
