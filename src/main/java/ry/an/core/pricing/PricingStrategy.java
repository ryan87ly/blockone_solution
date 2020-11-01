package ry.an.core.pricing;

import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutItems;
import ry.an.model.order.CheckoutResult;

import java.util.ArrayList;
import java.util.List;

public abstract class PricingStrategy {
    protected abstract PricingStrategyProcessResult doCalculate(CheckoutResult checkoutResult);

    public final CheckoutResult calculate(CheckoutResult checkoutResult) {
        // No remaining items, return directly
        if (checkoutResult.getRemainingItems().isEmpty()) {
            return checkoutResult;
        }

        // Calculate processed items for current strategy
        PricingStrategyProcessResult processResult = doCalculate(checkoutResult);

        // If there is no update, return directly
        if (processResult.isEmpty()) {
            return checkoutResult;
        }

        // Append processed items
        List<CheckoutItemResult> updatedProceededItems = new ArrayList<>(checkoutResult.getProcessedItems());
        updatedProceededItems.addAll(processResult.getProceededItems());

        // Update remaining checkout items
        CheckoutItems remainingCheckoutItems = checkoutResult.getRemainingItems();
        for (CheckoutItem remainingItem : processResult.getUpdatedRemainingItems()) {
            remainingCheckoutItems = remainingCheckoutItems.updateItem(remainingItem);
        }

        return CheckoutResult.of(updatedProceededItems, remainingCheckoutItems);
    }
}
