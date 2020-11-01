package ry.an.service.order;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ry.an.core.pricing.DiscountStrategyFactory;
import ry.an.core.pricing.PricingStrategy;
import ry.an.exception.ProductNotFoundException;
import ry.an.model.discount.DiscountEntity;
import ry.an.model.order.*;
import ry.an.model.product.Product;
import ry.an.service.discount.DiscountService;
import ry.an.service.product.ProductService;
import ry.an.util.IdGenerator;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final IdGenerator idGenerator;
    private final Clock clock;
    private final ProductService productService;
    private final DiscountService discountService;
    private final DiscountStrategyFactory discountStrategyFactory;
    private final PricingStrategy defaultPricingStrategy;

    private OrderServiceImpl(@Qualifier("orderIdGenerator") IdGenerator idGenerator, Clock clock, ProductService productService, DiscountService discountService, DiscountStrategyFactory discountStrategyFactory, PricingStrategy defaultPricingStrategy) {
        this.idGenerator = idGenerator;
        this.clock = clock;
        this.productService = productService;
        this.discountService = discountService;
        this.discountStrategyFactory = discountStrategyFactory;
        this.defaultPricingStrategy = defaultPricingStrategy;
    }

    public static OrderServiceImpl of(IdGenerator idGenerator, Clock clock, ProductService productService, DiscountService discountService, DiscountStrategyFactory discountStrategyFactory, PricingStrategy defaultPricingStrategy) {
        return new OrderServiceImpl(idGenerator, clock, productService, discountService, discountStrategyFactory, defaultPricingStrategy);
    }

    @Override
    public Order checkout(ShoppingCart shoppingCart) {
        // Ensure all products are existed
        List<CheckoutItem> checkoutItems = shoppingCart.getItems()
            .stream()
            .map(shoppingCartItem -> {
                Product product = productService.findActiveProductById(shoppingCartItem.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException(shoppingCartItem.getProductId()));
                return CheckoutItem.of(product, shoppingCartItem.getQuantity());
            })
            .collect(Collectors.toList());

        // Find available discounts for shopping cart items
        List<DiscountEntity> discountEntities = checkoutItems
            .stream()
            .flatMap(checkoutItem -> discountService.findActiveDiscountsForProduct(checkoutItem.getProduct().getId()).stream())
            .collect(Collectors.toList());

        // Build pricing strategy list
        List<PricingStrategy> pricingStrategies = new LinkedList<>();
        for (DiscountEntity discountEntity : discountEntities) {
            PricingStrategy discountStrategy = discountStrategyFactory.createStrategy(discountEntity);
            pricingStrategies.add(discountStrategy);
        }

        // Fallback strategy
        pricingStrategies.add(defaultPricingStrategy);

        CheckoutResult checkoutResult = CheckoutResult.of(Collections.emptyList(), CheckoutItems.of(checkoutItems));
        for (PricingStrategy pricingStrategy : pricingStrategies) {
            checkoutResult = pricingStrategy.calculate(checkoutResult);
        }

        String orderId = idGenerator.nextId();
        ZonedDateTime creationTime = ZonedDateTime.now(clock);

        return Order.of(orderId, checkoutResult.getProcessedItems(), creationTime);
    }
}
