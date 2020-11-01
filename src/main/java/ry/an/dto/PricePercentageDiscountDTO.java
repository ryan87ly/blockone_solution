package ry.an.dto;

public class PricePercentageDiscountDTO extends DiscountDTOBase {
    private String productId;
    private int discountOnPurchase;
    private int discountRate; // In 1%

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getDiscountOnPurchase() {
        return discountOnPurchase;
    }

    public void setDiscountOnPurchase(int discountOnPurchase) {
        this.discountOnPurchase = discountOnPurchase;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }
}
