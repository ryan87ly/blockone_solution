package ry.an.service.discount;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ry.an.model.discount.DiscountEntity;
import ry.an.repository.discount.DiscountRepository;
import ry.an.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final IdGenerator idGenerator;

    private DiscountServiceImpl(DiscountRepository discountRepository, @Qualifier("discountIdGenerator") IdGenerator idGenerator) {
        this.discountRepository = discountRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<DiscountEntity> findActiveDiscountById(String discountId) {
        return discountRepository.findDiscountById(discountId)
                .filter(DiscountEntity::isActive);
    }

    @Override
    public List<DiscountEntity> findActiveDiscountsForProduct(String productId) {
        return discountRepository.findsDiscountForProduct(productId);
    }

    @Override
    public List<DiscountEntity> allActiveDiscounts() {
        return discountRepository.allDiscounts()
                .stream()
                .filter(DiscountEntity::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public DiscountEntity createDiscount(DiscountEntity discount) {
        DiscountEntity createdDiscount = discount.withId(idGenerator.nextId());
        discountRepository.save(createdDiscount);
        return createdDiscount;
    }

    @Override
    public DiscountEntity updateDiscount(String id, DiscountEntity discount) {
        discountRepository.save(discount.withId(id));
        return discount;
    }

    @Override
    public void deactivateDiscount(String discountId) {
        Optional<DiscountEntity> discountOpt = discountRepository.findDiscountById(discountId);
        if (discountOpt.isEmpty() || !discountOpt.get().isActive()) {
            return;
        }
        DiscountEntity updatedDiscount = discountOpt.get().withActive(false);
        discountRepository.save(updatedDiscount);
    }
}
