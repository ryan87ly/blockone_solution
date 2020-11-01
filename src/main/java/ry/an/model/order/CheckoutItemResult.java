package ry.an.model.order;

import ry.an.model.product.Product;
import ry.an.model.price.Price;

import java.util.Objects;

public class CheckoutItemResult {
    private final Product product;
    private final int quantity;
    private final Price originalPrice;
    private final Price finalPrice;
    private final boolean discounted;

    private CheckoutItemResult(Product product, int quantity, Price originalPrice, Price finalPrice, boolean discounted) {
        this.product = product;
        this.quantity = quantity;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.discounted = discounted;
    }

    public static CheckoutItemResult of(Product product, int quantity, Price originalPrice, Price finalPrice, boolean discounted) {
        return new CheckoutItemResult(product, quantity, originalPrice, finalPrice, discounted);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Price getOriginalPrice() {
        return originalPrice;
    }

    public Price getFinalPrice() {
        return finalPrice;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckoutItemResult that = (CheckoutItemResult) o;
        return quantity == that.quantity &&
                discounted == that.discounted &&
                Objects.equals(product, that.product) &&
                Objects.equals(originalPrice, that.originalPrice) &&
                Objects.equals(finalPrice, that.finalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, originalPrice, finalPrice, discounted);
    }

    @Override
    public String toString() {
        return "CheckoutItemResult{" +
                "product=" + product +
                ", count=" + quantity +
                ", originalPrice=" + originalPrice +
                ", finalPrice=" + finalPrice +
                ", discounted=" + discounted +
                '}';
    }
}
