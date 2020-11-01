package ry.an.model.discount;

import java.util.Objects;

public class PricePercentageDiscount extends DiscountEntity {
    private final String productId;
    private final int discountOnPurchase;
    private final DiscountRate discountRate;

    private PricePercentageDiscount(String id, String description, boolean active, String productId, int discountedItemOnPurchase, DiscountRate discountRate) {
        super(id, DiscountType.PRICE_PERCENTAGE_REDUCTION, description, active);
        this.productId = productId;
        this.discountOnPurchase = discountedItemOnPurchase;
        this.discountRate = discountRate;
    }

    public static PricePercentageDiscount of(String id, String description, boolean active, String productId, int discountedItemOnPurchase, DiscountRate discountRate) {
        return new PricePercentageDiscount(id, description, active, productId, discountedItemOnPurchase, discountRate);
    }

    public String getProductId() {
        return productId;
    }

    public int getDiscountOnPurchase() {
        return discountOnPurchase;
    }

    public DiscountRate getDiscountRate() {
        return discountRate;
    }

    @Override
    public boolean canApplyOn(String productId) {
        return Objects.equals(this.productId, productId);
    }

    @Override
    public PricePercentageDiscount withId(String id) {
        return new PricePercentageDiscount(id, description, active, productId, discountOnPurchase, discountRate);
    }

    @Override
    public PricePercentageDiscount withActive(boolean isActive) {
        return new PricePercentageDiscount(id, description, isActive, productId, discountOnPurchase, discountRate);
    }
}
