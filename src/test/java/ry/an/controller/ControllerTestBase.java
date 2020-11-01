package ry.an.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ry.an.repository.discount.DiscountRepository;
import ry.an.repository.product.ProductRepository;
import ry.an.repository.shoppingcart.ShoppingCartRepository;

public abstract class ControllerTestBase {
    @Autowired
	protected MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeEach
    void setup() {
        productRepository.clear();
        discountRepository.clear();
        shoppingCartRepository.clear();
    }

}
