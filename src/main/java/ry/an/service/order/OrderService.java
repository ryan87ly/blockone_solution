package ry.an.service.order;

import ry.an.model.order.Order;
import ry.an.model.order.ShoppingCart;

public interface OrderService {
    /**
     * Checkout provided shopping cart. Generate a order record
     * @param shoppingCart
     * @return Order with all the details
     * @throws ry.an.exception.ProductNotFoundException when any of the products in shopping cart got removed
     */
    Order checkout(ShoppingCart shoppingCart);
}
