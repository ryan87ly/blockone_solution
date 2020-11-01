package ry.an.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ry.an.dto.OrderDTO;
import ry.an.dto.OrderItemDTO;
import ry.an.dto.ShoppingCartDTO;
import ry.an.dto.ShoppingCartItemDTO;
import ry.an.exception.ProductNotFoundException;
import ry.an.model.order.CheckoutItemResult;
import ry.an.model.order.Order;
import ry.an.model.order.ShoppingCart;
import ry.an.model.order.ShoppingCartItem;
import ry.an.service.order.OrderService;
import ry.an.service.product.ProductService;
import ry.an.service.shoppingcart.ShoppingCartService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping
    @ResponseBody
    public ShoppingCartDTO getShoppingCart() {
        ShoppingCart shoppingCart = shoppingCartService.getCurrentShoppingCart();
        return shoppingCartToDTO(shoppingCart);
    }

    @PostMapping("/additem")
    @ResponseBody
    public ShoppingCartDTO addItem(@RequestBody ShoppingCartItemDTO itemDTO) {
        InputValidations.requirePresent(productService.findActiveProductById(itemDTO.getProductId()), () -> "Product not found, id=" + itemDTO.getProductId());

        shoppingCartService.addItem(itemDTO.getProductId(), itemDTO.getQuantity());
        ShoppingCart shoppingCart = shoppingCartService.getCurrentShoppingCart();
        return shoppingCartToDTO(shoppingCart);
    }

    @PostMapping("/amenditem")
    @ResponseBody
    public ShoppingCartDTO amendItem(@RequestBody ShoppingCartItemDTO itemDTO) {
        InputValidations.requirePresent(productService.findActiveProductById(itemDTO.getProductId()), () -> "Product not found, id=" + itemDTO.getProductId());
        InputValidations.requirePresent(shoppingCartService.getItemByProductId(itemDTO.getProductId()), () -> "Product not found in shopping cart, id=" + itemDTO.getProductId());

        shoppingCartService.updateItemQuantity(itemDTO.getProductId(), itemDTO.getQuantity());
        ShoppingCart shoppingCart = shoppingCartService.getCurrentShoppingCart();
        return shoppingCartToDTO(shoppingCart);
    }

    @PostMapping("/removeitem")
    @ResponseBody
    public ShoppingCartDTO removeItem(@RequestBody JsonNode requestBody) {
        JsonNode productIdObj = requestBody.get("productId");
        InputValidations.require(Objects.nonNull(productIdObj) && productIdObj.isTextual(), () -> "'productId' is mandatory");
        String productId = productIdObj.asText();
        InputValidations.requirePresent(shoppingCartService.getItemByProductId(productId), () -> "Product not found in shopping cart, id=" + productId);

        shoppingCartService.removeItem(productId);
        ShoppingCart shoppingCart = shoppingCartService.getCurrentShoppingCart();
        return shoppingCartToDTO(shoppingCart);
    }

    @PostMapping("/checkout")
    @ResponseBody
    public OrderDTO checkout() {
        ShoppingCart shoppingCart = shoppingCartService.getCurrentShoppingCart();
        InputValidations.require(!shoppingCart.isEmpty(), () -> "Shopping Cart is empty, nothing to checkout");

        try {
            Order order = orderService.checkout(shoppingCart);
            shoppingCartService.clear();
            return orderToDTO(order);
        } catch (ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    private ShoppingCartDTO shoppingCartToDTO(ShoppingCart shoppingCart) {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        List<ShoppingCartItemDTO> items = shoppingCart.getItems()
                .stream()
                .map(this::shoppingCartItemToDTO)
                .collect(Collectors.toList());
        shoppingCartDTO.setItems(items);
        return shoppingCartDTO;
    }

    private ShoppingCartItemDTO shoppingCartItemToDTO(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItemDTO shoppingCartItemDTO = new ShoppingCartItemDTO();
        shoppingCartItemDTO.setProductId(shoppingCartItem.getProductId());
        shoppingCartItemDTO.setQuantity(shoppingCartItem.getQuantity());
        return shoppingCartItemDTO;
    }

    private OrderDTO orderToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setCreationTime(order.getCreationTimeStr());
        orderDTO.setOriginalTotalPrice(order.getOriginalTotalPrice().getValueInCents());
        orderDTO.setFinalTotalPrice(order.getFinalTotalPrice().getValueInCents());
        orderDTO.setItems(
            order.getItems()
                .stream()
                .map(this::checkoutItemResultToDTO)
                .collect(Collectors.toList())
        );
        return orderDTO;
    }

    private OrderItemDTO checkoutItemResultToDTO(CheckoutItemResult checkoutItemResult) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setProductId(checkoutItemResult.getProduct().getId());
        orderItemDTO.setDescription(checkoutItemResult.getProduct().getDescription());
        orderItemDTO.setQuantity(checkoutItemResult.getQuantity());
        orderItemDTO.setOriginalPrice(checkoutItemResult.getOriginalPrice().getValueInCents());
        orderItemDTO.setFinalPrice(checkoutItemResult.getFinalPrice().getValueInCents());
        orderItemDTO.setDiscounted(checkoutItemResult.isDiscounted());
        return orderItemDTO;
    }


}