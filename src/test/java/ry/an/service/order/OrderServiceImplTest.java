package ry.an.service.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ry.an.core.pricing.DiscountStrategyFactory;
import ry.an.core.pricing.DiscountStrategyFactoryImpl;
import ry.an.core.pricing.OriginalPriceStrategy;
import ry.an.core.pricing.PricingStrategy;
import ry.an.exception.ProductNotFoundException;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.Order;
import ry.an.model.order.ShoppingCart;
import ry.an.model.order.ShoppingCartItem;
import ry.an.model.price.Price;
import ry.an.service.discount.DiscountService;
import ry.an.service.product.ProductService;
import ry.an.util.CountingIdGenerator;
import ry.an.util.IdGenerator;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ry.an.util.DiscountsForTesting.bundleDiscount3;
import static ry.an.util.DiscountsForTesting.pricingDiscount1;
import static ry.an.util.ProductsForTesting.*;

class OrderServiceImplTest {

    private IdGenerator idGenerator;
    private Clock clock;
    private DiscountStrategyFactory discountStrategyFactory;
    private PricingStrategy defaultPricingStrategy;
    private ProductService productService;

    @Mock
    private DiscountService discountService;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        productService = mockedProductService();
        discountStrategyFactory = DiscountStrategyFactoryImpl.of(productService);
        idGenerator = new CountingIdGenerator(0);
        clock = Clock.fixed(ZonedDateTime.of(2020, 10, 30, 10, 0, 0, 0, ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        defaultPricingStrategy = OriginalPriceStrategy.INSTANCE;

        orderService = OrderServiceImpl.of(idGenerator, clock, productService, discountService, discountStrategyFactory, defaultPricingStrategy);
    }

    @Test
    void testCheckoutShoppingCartWhenNoDiscountAvailable() {
        when(discountService.findActiveDiscountsForProduct(anyString())).thenReturn(List.of());

        ShoppingCart shoppingCart = ShoppingCart.of(
            List.of(
                ShoppingCartItem.of(testProduct1.getId(), 5),
                ShoppingCartItem.of(testProduct2.getId(), 3),
                ShoppingCartItem.of(testProduct3.getId(), 6)
            )
        );
        Order order = orderService.checkout(shoppingCart);

        assertNotNull(order);
        assertEquals("1", order.getId());
        assertEquals(ZonedDateTime.of(2020, 10, 30, 10, 0, 0, 0, ZoneOffset.UTC), order.getCreationTime());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct1, 5, Price.fromDollar(1005), Price.fromDollar(1005), false),
                CheckoutItemResult.of(testProduct2, 3, Price.fromDollar(156), Price.fromDollar(156), false),
                CheckoutItemResult.of(testProduct3, 6, Price.fromDollar(600), Price.fromDollar(600), false)
            ),
            order.getItems()
        );
        assertEquals(Price.fromDollar(1761), order.getFinalTotalPrice());
        assertEquals(Price.fromDollar(1761), order.getOriginalTotalPrice());
    }

    @Test
    void testCheckoutShoppingCartWhenDiscountsAreAvailable() {
        // 10% off for testProduct1
        when(discountService.findActiveDiscountsForProduct(testProduct1.getId())).thenReturn(List.of(pricingDiscount1));

        // Free testProduct3
        when(discountService.findActiveDiscountsForProduct(testProduct2.getId())).thenReturn(List.of(bundleDiscount3));

        ShoppingCart shoppingCart = ShoppingCart.of(
            List.of(
                ShoppingCartItem.of(testProduct1.getId(), 10),
                ShoppingCartItem.of(testProduct2.getId(), 3)
            )
        );
        Order order = orderService.checkout(shoppingCart);

        assertNotNull(order);
        assertEquals("1", order.getId());
        assertEquals(ZonedDateTime.of(2020, 10, 30, 10, 0, 0, 0, ZoneOffset.UTC), order.getCreationTime());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct1, 10, Price.fromDollar(2010), Price.fromDollar(1809), true),
                CheckoutItemResult.of(testProduct2, 3, Price.fromDollar(156), Price.fromDollar(156), false),
                CheckoutItemResult.of(testProduct3, 3, Price.fromDollar(300), Price.ZERO, true)
            ),
            order.getItems()
        );
        assertEquals(Price.fromDollar(2466), order.getOriginalTotalPrice());
        assertEquals(Price.fromDollar(1965), order.getFinalTotalPrice());
    }

    @Test
    void exceptionIsRaisedWhenShoppingCartIsInactive() {
        when(discountService.findActiveDiscountsForProduct(anyString())).thenReturn(List.of());

        ShoppingCart shoppingCart = ShoppingCart.of(
            List.of(
                ShoppingCartItem.of(testProduct1.getId(), 5),
                ShoppingCartItem.of(testProduct3.getId(), 6),
                ShoppingCartItem.of(testProduct4.getId(), 10) // testProduct4 is inactive
            )
        );

        try {
            orderService.checkout(shoppingCart);
            fail();
        } catch (ProductNotFoundException e) {
            assertEquals("Product not found, id=" +testProduct4.getId(), e.getMessage());
        }
    }
}