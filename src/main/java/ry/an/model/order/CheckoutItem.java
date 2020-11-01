package ry.an.model.order;

import ry.an.model.product.Product;
import java.util.Objects;

public final class CheckoutItem {
    private final Product product;
    private final int quantity;

    private CheckoutItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public static CheckoutItem of(Product product, int quantity) {
        return new CheckoutItem(product, quantity);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckoutItem that = (CheckoutItem) o;
        return quantity == that.quantity &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }
}
