package ry.an.repository.shoppingcart;

import ry.an.model.order.ShoppingCartItem;

import java.util.Collection;
import java.util.Optional;

public interface ShoppingCartRepository {
    void addItem(String productId, int quantity);
    void updateItemQuantity(String productId, int quantity);
    void removeItem(String productId);
    Optional<ShoppingCartItem> getItem(String productId);
    Collection<ShoppingCartItem> getShoppingCartItems();
    void clear();
}
