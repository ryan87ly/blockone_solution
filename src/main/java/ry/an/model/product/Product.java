package ry.an.model.product;

import ry.an.model.price.Price;

import java.util.Objects;

public final class Product {
    private final String id;
    private final String description;
    private final Price price;
    private final boolean active;

    private Product(String id, String description, Price price, boolean active) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.active = active;
    }

    public static Product of(String id, String description, Price price, boolean active) {
        return new Product(id, description, price, active);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Price getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public Product withActive(boolean isActive) {
        return new Product(id, description, price, isActive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return active == product.active &&
                Objects.equals(id, product.id) &&
                Objects.equals(description, product.description) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, price, active);
    }
}
