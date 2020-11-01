package ry.an.exception;

public class ShoppingCartItemNotFoundException extends RuntimeException {
    private final String productId;

    public ShoppingCartItemNotFoundException(String productId) {
        this.productId = productId;
    }

    @Override
    public String getMessage() {
        return String.format("Item not found in shopping cart, product id=%s", productId);
    }
}
