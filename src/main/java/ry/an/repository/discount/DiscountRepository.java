package ry.an.repository.discount;

import ry.an.model.discount.DiscountEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository {
    Optional<DiscountEntity> findDiscountById(String id);
    List<DiscountEntity> findsDiscountForProduct(String productId);
    Collection<DiscountEntity> allDiscounts();
    void save(DiscountEntity discount);
    void clear();
}
