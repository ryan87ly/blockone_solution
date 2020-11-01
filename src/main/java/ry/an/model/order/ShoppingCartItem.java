package ry.an.model.order;

import java.util.Objects;

public final class ShoppingCartItem {
    private final String productId;
    private final int quantity;

    private ShoppingCartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static ShoppingCartItem of(String productId, int quantity) {
        return new ShoppingCartItem(productId, quantity);
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItem that = (ShoppingCartItem) o;
        return quantity == that.quantity &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity);
    }
}
