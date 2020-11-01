package ry.an.model.discount;

public abstract class DiscountEntity {
    protected final String id;
    protected final DiscountType productDiscountType;
    protected final String description;
    protected final boolean active;

    protected DiscountEntity(String id, DiscountType productDiscountType, String description, boolean active) {
        this.id = id;
        this.productDiscountType = productDiscountType;
        this.description = description;
        this.active = active;
    }

    public abstract boolean canApplyOn(String productId);
    public abstract DiscountEntity withId(String id);
    public abstract DiscountEntity withActive(boolean isActive);

    public String getId() {
        return id;
    }

    public DiscountType getProductDiscountType() {
        return productDiscountType;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}
