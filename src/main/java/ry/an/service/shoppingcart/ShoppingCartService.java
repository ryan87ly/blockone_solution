package ry.an.service.shoppingcart;

import ry.an.model.order.ShoppingCart;
import ry.an.model.order.ShoppingCartItem;

import java.util.Optional;

public interface ShoppingCartService {
    void addItem(String productId, int quantity);
    void updateItemQuantity(String productId, int quantity);
    void removeItem(String productId);
    void clear();
    Optional<ShoppingCartItem> getItemByProductId(String productId);
    ShoppingCart getCurrentShoppingCart();
}
