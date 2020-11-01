package ry.an.model.order;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class ShoppingCart {
    private final Map<String, ShoppingCartItem> itemsMap;

    private ShoppingCart(Map<String, ShoppingCartItem> itemsMap) {
        this.itemsMap = itemsMap;
    }

    public static ShoppingCart of(Map<String, ShoppingCartItem> itemsMap) {
        return new ShoppingCart(itemsMap);
    }

    public static ShoppingCart of(Collection<ShoppingCartItem> items) {
        Map<String, ShoppingCartItem> itemMap = items
                .stream()
                .collect(Collectors.toMap(
                    ShoppingCartItem::getProductId,
                    item -> item,
                    (v1, v2) -> v2,
                    LinkedHashMap::new
                ));
        return new ShoppingCart(itemMap);
    }

    public Collection<ShoppingCartItem> getItems() {
        return itemsMap.values();
    }

    public boolean isEmpty() {
        return itemsMap.isEmpty();
    }
}
