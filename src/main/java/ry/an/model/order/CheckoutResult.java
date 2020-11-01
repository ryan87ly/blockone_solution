package ry.an.model.order;

import ry.an.model.price.Price;

import java.util.List;

public final class CheckoutResult {
    private final List<CheckoutItemResult> processedItems;
    private final CheckoutItems remainingItems;

    private CheckoutResult(List<CheckoutItemResult> processedItems, CheckoutItems remainingItems) {
        this.processedItems = processedItems;
        this.remainingItems = remainingItems;
    }

    public static CheckoutResult of(List<CheckoutItemResult> processedItems, CheckoutItems remainingItems) {
        return new CheckoutResult(processedItems, remainingItems);
    }

    public Price getCurrentPrice() {
        return processedItems.stream()
                .map(CheckoutItemResult::getFinalPrice)
                .reduce(Price.ZERO, Price::add);
    }

    public List<CheckoutItemResult> getProcessedItems() {
        return processedItems;
    }

    public CheckoutItems getRemainingItems() {
        return remainingItems;
    }
}
