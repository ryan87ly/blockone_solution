package ry.an.service.discount;

import ry.an.model.discount.DiscountEntity;

import java.util.List;
import java.util.Optional;

public interface DiscountService {
    Optional<DiscountEntity> findActiveDiscountById(String discountId);
    List<DiscountEntity> findActiveDiscountsForProduct(String productId);
    List<DiscountEntity> allActiveDiscounts();
    DiscountEntity createDiscount(DiscountEntity discount);
    DiscountEntity updateDiscount(String id, DiscountEntity discount);
    void deactivateDiscount(String discountId);
}
