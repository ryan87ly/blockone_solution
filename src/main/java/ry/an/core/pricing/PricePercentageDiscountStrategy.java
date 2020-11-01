package ry.an.core.pricing;

import ry.an.model.discount.PricePercentageDiscount;
import ry.an.model.order.CheckoutItem;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.CheckoutResult;
import ry.an.model.price.Price;
import ry.an.model.product.Product;

import java.util.List;
import java.util.Objects;

public class PricePercentageDiscountStrategy extends PricingStrategy {
    private final PricePercentageDiscount discountDefinition;

    private PricePercentageDiscountStrategy(PricePercentageDiscount discountDefinition) {
        this.discountDefinition = discountDefinition;
    }

    public static PricePercentageDiscountStrategy of(PricePercentageDiscount discountDefinition) {
        return new PricePercentageDiscountStrategy(discountDefinition);
    }

    @Override
    protected PricingStrategyProcessResult doCalculate(CheckoutResult checkoutResult) {
        String discountedProductId = discountDefinition.getProductId();
        CheckoutItem checkoutItem = checkoutResult.getRemainingItems().getItemByProductId(discountedProductId);
        if (Objects.isNull(checkoutItem) || checkoutItem.getQuantity() < discountDefinition.getDiscountOnPurchase()) {
            return PricingStrategyProcessResult.EMPTY;
        }
        Product checkoutProduct = checkoutItem.getProduct();
        int purchaseQuantity = checkoutItem.getQuantity();
        int discountedQuantity = purchaseQuantity / discountDefinition.getDiscountOnPurchase();
        Price originalPrice = checkoutProduct.getPrice().multiply(discountedQuantity);
        Price discountedPrice = originalPrice.subtract(originalPrice.multiply(discountDefinition.getDiscountRate()));
        CheckoutItemResult itemResult = CheckoutItemResult.of(checkoutProduct, discountedQuantity, originalPrice, discountedPrice,true);

        int remainingQuantity = purchaseQuantity - discountedQuantity;
        CheckoutItem remainingCheckoutItem = CheckoutItem.of(checkoutProduct, remainingQuantity);

        return PricingStrategyProcessResult.of(List.of(itemResult), List.of(remainingCheckoutItem));
    }
}
