package ry.an.core.pricing;

import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;

import java.util.List;

class PricingStrategyProcessResult {
    static final PricingStrategyProcessResult EMPTY = new PricingStrategyProcessResult(List.of(), List.of());

    private final List<CheckoutItemResult> proceededItems;
    private final List<CheckoutItem> updatedRemainingItems;

    private PricingStrategyProcessResult(List<CheckoutItemResult> proceededItems, List<CheckoutItem> remainingItems) {
        this.proceededItems = proceededItems;
        this.updatedRemainingItems = remainingItems;
    }

    public static PricingStrategyProcessResult of(List<CheckoutItemResult> proceededItems, List<CheckoutItem> remainingItems) {
        return new PricingStrategyProcessResult(proceededItems, remainingItems);
    }


    List<CheckoutItemResult> getProceededItems() {
        return proceededItems;
    }

    List<CheckoutItem> getUpdatedRemainingItems() {
        return updatedRemainingItems;
    }

    boolean isEmpty() {
        return proceededItems.isEmpty() && updatedRemainingItems.isEmpty();
    }
}
