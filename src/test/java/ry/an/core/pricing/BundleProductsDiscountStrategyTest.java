package ry.an.core.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ry.an.exception.ProductNotFoundException;
import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutItems;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;
import ry.an.service.product.ProductService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ry.an.util.DiscountsForTesting.bundleDiscount1;
import static ry.an.util.DiscountsForTesting.bundleDiscount2;
import static ry.an.util.ProductsForTesting.*;

class BundleProductsDiscountStrategyTest {

    @Mock
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        when(productService.findProductById(anyString())).thenReturn(Optional.empty());
        when(productService.findProductById(eq(testProduct1.getId()))).thenReturn(Optional.of(testProduct1));
        when(productService.findProductById(eq(testProduct2.getId()))).thenReturn(Optional.of(testProduct2));
        when(productService.findProductById(eq(testProduct3.getId()))).thenReturn(Optional.of(testProduct3));
    }


    @Test
    void testThatFreeProductsWillBeRecorded() {
        BundleProductsDiscountStrategy strategy = BundleProductsDiscountStrategy.of(bundleDiscount1, productService);

        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 40),
                CheckoutItem.of(testProduct3, 30)
            )
        );
        List<CheckoutItemResult> currentCheckoutResults = List.of(
            CheckoutItemResult.of(testProduct2, 50, Price.fromDollar(2600), Price.fromDollar(2500), true)
        );
        CheckoutResult originCheckoutResult = CheckoutResult.of(currentCheckoutResults, remainingItems);

        CheckoutResult result = strategy.calculate(originCheckoutResult);
        assertEquals(Price.fromDollar(10540), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct2, 50, Price.fromDollar(2600), Price.fromDollar(2500), true),
                CheckoutItemResult.of(testProduct1, 40, Price.fromDollar(8040), Price.fromDollar(8040), false),
                CheckoutItemResult.of(testProduct2, 40, Price.fromDollar(2080), Price.ZERO, true)
            ),
            result.getProcessedItems()
        );
    }

    @Test
    void testNoProcessedItem() {
        BundleProductsDiscountStrategy strategy = BundleProductsDiscountStrategy.of(bundleDiscount2, productService);

        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 40)
            )
        );

        CheckoutResult originCheckoutResult = CheckoutResult.of(Collections.emptyList(), remainingItems);

        CheckoutResult result = strategy.calculate(originCheckoutResult);
        assertEquals(Price.fromDollar(8040), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct1, 40, Price.fromDollar(8040), Price.fromDollar(8040), false),
                CheckoutItemResult.of(testProduct2, 40, Price.fromDollar(2080), Price.ZERO, true),
                CheckoutItemResult.of(testProduct3, 40, Price.fromDollar(4000), Price.ZERO, true)
            ),
            result.getProcessedItems()
        );
    }

    @Test
    void testThatExceptionIsRaisedWhenFreeProductsAreMissing() {
        when(productService.findProductById(anyString())).thenReturn(Optional.empty());

        BundleProductsDiscountStrategy strategy = BundleProductsDiscountStrategy.of(bundleDiscount2, productService);

        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 40)
            )
        );

        CheckoutResult originCheckoutResult = CheckoutResult.of(Collections.emptyList(), remainingItems);

        try {
            strategy.calculate(originCheckoutResult);
            fail();
        } catch (ProductNotFoundException e) {
            assertEquals("Product not found, id=" + testProduct2.getId(), e.getMessage());
        }
    }

}