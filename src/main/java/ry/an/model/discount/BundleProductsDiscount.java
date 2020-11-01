package ry.an.model.discount;

import java.util.List;
import java.util.Objects;

public class BundleProductsDiscount extends DiscountEntity {
    private final String productId;
    private final List<String> freeProductIds;

    private BundleProductsDiscount(String id, String description, boolean active, String productId, List<String> freeProducts) {
        super(id, DiscountType.BUNDLE_PRODUCTS, description, active);
        this.productId = productId;
        this.freeProductIds = freeProducts;
    }

    public static BundleProductsDiscount of(String id, String description, boolean active, String productId, List<String> freeProducts) {
        return new BundleProductsDiscount(id, description, active, productId, freeProducts);
    }

    public String getProductId() {
        return productId;
    }

    public List<String> getFreeProductIds() {
        return freeProductIds;
    }

    @Override
    public boolean canApplyOn(String productId) {
        return Objects.equals(this.productId, productId);
    }

    @Override
    public BundleProductsDiscount withId(String id) {
        return new BundleProductsDiscount(id, description, active, productId, freeProductIds);
    }

    @Override
    public BundleProductsDiscount withActive(boolean isActive) {
        return new BundleProductsDiscount(id, description, isActive, productId, freeProductIds);
    }
}
