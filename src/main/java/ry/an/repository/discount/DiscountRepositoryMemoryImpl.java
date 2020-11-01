package ry.an.repository.discount;

import ry.an.model.discount.DiscountEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * JVM memory DiscountRepository implementation
 */
public class DiscountRepositoryMemoryImpl implements DiscountRepository {
    private final ConcurrentMap<String, DiscountEntity> discountMap;

    private DiscountRepositoryMemoryImpl(ConcurrentMap<String, DiscountEntity> discountMap) {
        this.discountMap = discountMap;
    }

    public static DiscountRepositoryMemoryImpl empty() {
        return new DiscountRepositoryMemoryImpl(new ConcurrentHashMap<>());
    }

    @Override
    public Optional<DiscountEntity> findDiscountById(String id) {
        return Optional.ofNullable(discountMap.get(id));
    }

    @Override
    public List<DiscountEntity> findsDiscountForProduct(String productId) {
        return discountMap.values()
                .stream()
                .filter(discountEntity -> discountEntity.canApplyOn(productId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<DiscountEntity> allDiscounts() {
        return discountMap.values();
    }

    @Override
    public void save(DiscountEntity discount) {
        discountMap.put(discount.getId(), discount);
    }

    @Override
    public void clear() {
        discountMap.clear();
    }
}
