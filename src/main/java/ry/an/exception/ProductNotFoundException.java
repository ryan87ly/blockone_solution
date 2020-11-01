package ry.an.exception;

public class ProductNotFoundException extends RuntimeException {
    private final String productId;

    public ProductNotFoundException(String productId) {
        this.productId = productId;
    }

    @Override
    public String getMessage() {
        return String.format("Product not found, id=%s", productId);
    }
}
