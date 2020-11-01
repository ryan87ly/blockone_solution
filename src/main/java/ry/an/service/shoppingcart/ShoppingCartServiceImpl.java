package ry.an.service.shoppingcart;

import org.springframework.stereotype.Service;
import ry.an.model.order.ShoppingCart;
import ry.an.model.order.ShoppingCartItem;
import ry.an.repository.shoppingcart.ShoppingCartRepository;

import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    private ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public static ShoppingCartServiceImpl of(ShoppingCartRepository shoppingCartRepository) {
        return new ShoppingCartServiceImpl(shoppingCartRepository);
    }

    @Override
    public void addItem(String productId, int quantity) {
        shoppingCartRepository.addItem(productId, quantity);
    }

    @Override
    public void updateItemQuantity(String productId, int quantity) {
        shoppingCartRepository.updateItemQuantity(productId, quantity);
    }

    @Override
    public void removeItem(String productId) {
        shoppingCartRepository.removeItem(productId);
    }

    @Override
    public void clear() {
        shoppingCartRepository.clear();
    }

    @Override
    public Optional<ShoppingCartItem> getItemByProductId(String productId) {
        return shoppingCartRepository.getItem(productId);
    }

    @Override
    public ShoppingCart getCurrentShoppingCart() {
        return ShoppingCart.of(shoppingCartRepository.getShoppingCartItems());
    }
}