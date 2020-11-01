package ry.an.repository.shoppingcart;

import ry.an.model.order.ShoppingCartItem;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ShoppingCartRepositoryMemoryImpl implements ShoppingCartRepository {
    private ConcurrentMap<String, ShoppingCartItem> itemMap;

    private ShoppingCartRepositoryMemoryImpl(ConcurrentMap<String, ShoppingCartItem> itemMap) {
        this.itemMap = itemMap;
    }

    public static ShoppingCartRepositoryMemoryImpl empty() {
        return new ShoppingCartRepositoryMemoryImpl(new ConcurrentHashMap<>());
    }

    @Override
    public void addItem(String productId, int quantity) {
        itemMap.put(productId, ShoppingCartItem.of(productId, quantity));
    }

    @Override
    public void updateItemQuantity(String productId, int quantity) {
        addItem(productId, quantity);
    }

    @Override
    public void removeItem(String productId) {
        itemMap.remove(productId);
    }

    @Override
    public void clear() {
        itemMap.clear();
    }

    @Override
    public Optional<ShoppingCartItem> getItem(String productId) {
        return Optional.ofNullable(itemMap.get(productId));
    }

    @Override
    public Collection<ShoppingCartItem> getShoppingCartItems() {
        return itemMap.values();
    }
}
