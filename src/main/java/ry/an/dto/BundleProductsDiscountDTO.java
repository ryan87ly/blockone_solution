package ry.an.dto;

import java.util.List;

public class BundleProductsDiscountDTO extends DiscountDTOBase {
    private String productId;
    private List<String> freeProducts;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<String> getFreeProducts() {
        return freeProducts;
    }

    public void setFreeProducts(List<String> freeProducts) {
        this.freeProducts = freeProducts;
    }
}
