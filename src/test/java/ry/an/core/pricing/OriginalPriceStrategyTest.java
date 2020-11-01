package ry.an.core.pricing;

import org.junit.jupiter.api.Test;
import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutItems;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ry.an.util.ProductsForTesting.*;

class OriginalPriceStrategyTest {
    @Test
    void testCalculateForAllRemainingItems() {
        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 10),
                CheckoutItem.of(testProduct2, 20),
                CheckoutItem.of(testProduct3, 30)
            )
        );
        CheckoutResult originCheckoutResult = CheckoutResult.of(Collections.emptyList(), remainingItems);

        CheckoutResult result = OriginalPriceStrategy.INSTANCE.calculate(originCheckoutResult);

        assertEquals(Price.fromDollar(6050), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct1, 10, Price.fromDollar(2010), Price.fromDollar(2010), false),
                CheckoutItemResult.of(testProduct2, 20, Price.fromDollar(1040), Price.fromDollar(1040), false),
                CheckoutItemResult.of(testProduct3, 30, Price.fromDollar(3000), Price.fromDollar(3000), false)
            ),
            result.getProcessedItems()
        );
        assertTrue(result.getRemainingItems().isEmpty());
    }

    @Test
    void testCalculateAppendsCheckoutResults() {
        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 20),
                CheckoutItem.of(testProduct3, 40)
            )
        );
        List<CheckoutItemResult> currentCheckoutResults = List.of(
            CheckoutItemResult.of(testProduct2, 50, Price.fromDollar(2600), Price.fromDollar(2500), true)
        );

        CheckoutResult originCheckoutResult = CheckoutResult.of(currentCheckoutResults, remainingItems);

        CheckoutResult result = OriginalPriceStrategy.INSTANCE.calculate(originCheckoutResult);
        assertEquals(Price.fromDollar(10520), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct2, 50, Price.fromDollar(2600), Price.fromDollar(2500), true),
                CheckoutItemResult.of(testProduct1, 20, Price.fromDollar(4020), Price.fromDollar(4020), false),
                CheckoutItemResult.of(testProduct3, 40, Price.fromDollar(4000), Price.fromDollar(4000), false)
            ),
            result.getProcessedItems()
        );
        assertTrue(result.getRemainingItems().isEmpty());
    }
}