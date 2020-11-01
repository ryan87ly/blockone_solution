package ry.an.model.order;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class CheckoutItems {
    private final Map<String, CheckoutItem> items;

    private CheckoutItems(Map<String, CheckoutItem> items) {
        this.items = items;
    }

    public static CheckoutItems of(Map<String, CheckoutItem> items) {
        return new CheckoutItems(items);
    }

    public static CheckoutItems of(Collection<CheckoutItem> items) {
        Map<String, CheckoutItem> itemMap = items.stream()
            .collect(
                Collectors.toMap(
                    item -> item.getProduct().getId(),
                    item -> item,
                    (v1, v2) -> v2,
                    LinkedHashMap::new
                )
            );
        return of(itemMap);
    }

    public Collection<CheckoutItem> getItems() {
        return items.values();
    }

    public CheckoutItem getItemByProductId(String productId) {
        return items.get(productId);
    }

    public CheckoutItems updateItem(CheckoutItem item) {
        Map<String, CheckoutItem> updatedItemMap = new LinkedHashMap<>(items);
        if (item.getQuantity() == 0) {
            updatedItemMap.remove(item.getProduct().getId());
        } else {
            updatedItemMap.put(item.getProduct().getId(), item);
        }
        return of(updatedItemMap);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
