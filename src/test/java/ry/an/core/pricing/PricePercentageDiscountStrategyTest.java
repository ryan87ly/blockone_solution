package ry.an.core.pricing;

import org.junit.jupiter.api.Test;
import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutItems;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ry.an.util.DiscountsForTesting.pricingDiscount1;
import static ry.an.util.DiscountsForTesting.pricingDiscount2;
import static ry.an.util.ProductsForTesting.*;

class PricePercentageDiscountStrategyTest {

    @Test
    void testCalculateForDiscountedItems() {
        PricePercentageDiscountStrategy strategy = PricePercentageDiscountStrategy.of(pricingDiscount2);

        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 10),
                CheckoutItem.of(testProduct2, 20),
                CheckoutItem.of(testProduct3, 30)
            )
        );
        CheckoutResult originCheckoutResult = CheckoutResult.of(Collections.emptyList(), remainingItems);

        CheckoutResult result = strategy.calculate(originCheckoutResult);
        assertEquals(Price.fromDollar(260), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct2, 10, Price.fromDollar(520), Price.fromDollar(260), true)
            ),
            result.getProcessedItems()
        );

        CheckoutItems remainingItemsAfterCalculation = result.getRemainingItems();
        assertEquals(CheckoutItem.of(testProduct1, 10), remainingItemsAfterCalculation.getItemByProductId(testProduct1.getId()));
        assertEquals(CheckoutItem.of(testProduct2, 10), remainingItemsAfterCalculation.getItemByProductId(testProduct2.getId()));
        assertEquals(CheckoutItem.of(testProduct3, 30), remainingItemsAfterCalculation.getItemByProductId(testProduct3.getId()));
    }

    @Test
    void testCalculateForSingleDiscountedItems() {
        PricePercentageDiscountStrategy strategy = PricePercentageDiscountStrategy.of(pricingDiscount1);

        CheckoutItems remainingItems = CheckoutItems.of(
            List.of(
                CheckoutItem.of(testProduct1, 40),
                CheckoutItem.of(testProduct2, 20),
                CheckoutItem.of(testProduct3, 30)
            )
        );
        CheckoutResult originCheckoutResult = CheckoutResult.of(Collections.emptyList(), remainingItems);

        CheckoutResult result = strategy.calculate(originCheckoutResult);
        assertEquals(Price.fromDollar(7236), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct1, 40, Price.fromDollar(8040), Price.fromDollar(7236), true)
            ),
            result.getProcessedItems()
        );

        CheckoutItems remainingItemsAfterCalculation = result.getRemainingItems();
        assertNull(remainingItemsAfterCalculation.getItemByProductId("P-1"));
        assertEquals(CheckoutItem.of(testProduct2, 20), remainingItemsAfterCalculation.getItemByProductId(testProduct2.getId()));
        assertEquals(CheckoutItem.of(testProduct3, 30), remainingItemsAfterCalculation.getItemByProductId(testProduct3.getId()));
    }

    @Test
    void testCalculateWhenThereAreProceededItems() {
        PricePercentageDiscountStrategy strategy = PricePercentageDiscountStrategy.of(pricingDiscount1);

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
        assertEquals(Price.fromDollar(2500), originCheckoutResult.getCurrentPrice());

        CheckoutResult result = strategy.calculate(originCheckoutResult);
        assertEquals(Price.fromDollar(9736), result.getCurrentPrice());
        assertEquals(
            List.of(
                CheckoutItemResult.of(testProduct2, 50, Price.fromDollar(2600), Price.fromDollar(2500), true),
                CheckoutItemResult.of(testProduct1, 40, Price.fromDollar(8040), Price.fromDollar(7236), true)
            ),
            result.getProcessedItems()
        );
        assertNull(result.getRemainingItems().getItemByProductId(testProduct1.getId()));
        assertNull(result.getRemainingItems().getItemByProductId(testProduct2.getId()));
        assertEquals(CheckoutItem.of(testProduct3, 30), result.getRemainingItems().getItemByProductId(testProduct3.getId()));
    }
}