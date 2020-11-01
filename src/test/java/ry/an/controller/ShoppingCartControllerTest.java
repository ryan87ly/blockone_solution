package ry.an.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ry.an.dto.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ry.an.controller.ControllerTestUtil.*;
import static ry.an.util.JsonUtil.toJsonObject;
import static ry.an.util.JsonUtil.toJsonString;
import static ry.an.util.MvcResultUtil.getContentAsJson;
import static ry.an.util.MvcResultUtil.getContentAsObject;

@SpringBootTest
@AutoConfigureMockMvc
class ShoppingCartControllerTest extends ControllerTestBase {

    @Test
    void testReturnsEmptyItemListWhenShoppingCartIsEmpty() throws Exception {
        MvcResult getShoppingCartResult = mockMvc.perform(get("/shoppingcart"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseData = getContentAsJson(getShoppingCartResult);

        assertFalse(responseData.isNull());
        assertTrue(responseData.get("items").isEmpty());
    }

    @Test
    void testAddItemsToShoppingCart() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);

        // Verify it is able to add product1 to shopping cart
        MvcResult result = mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Shopping cart items are as expected
        JsonNode addProduct1ResponseData = getContentAsJson(result);
        List<JsonNode> items = Lists.newArrayList(addProduct1ResponseData.get("items"));
        assertEquals(1, items.size());
        assertTrue(items.contains(toJsonObject(Map.of("productId", product1.getId(), "quantity", 1))));

        // Add product 2
        MvcResult addProduct2Result = mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product2.getId(), "quantity", 10)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Items are as expected
        JsonNode addProduct2ResponseData = getContentAsJson(addProduct2Result);
        List<JsonNode> items2 = Lists.newArrayList(addProduct2ResponseData.get("items"));
        assertEquals(2, items2.size());
        assertTrue(items2.contains(toJsonObject(Map.of("productId", product1.getId(), "quantity", 1))));
        assertTrue(items2.contains(toJsonObject(Map.of("productId", product2.getId(), "quantity", 10))));
    }

    @Test
    void testAddUnknownProductReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", "unknown", "quantity", 1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testAmendShoppingCartItem() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);

        // Add product 1, quantity 50
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 50)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Add product 2, quantity 10
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product2.getId(), "quantity", 10)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Amend product 1 quantity to 30
        MvcResult result = mockMvc.perform(post("/shoppingcart/amenditem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 30)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Shopping cart items are as expected
        JsonNode responseData = getContentAsJson(result);
        List<JsonNode> items = Lists.newArrayList(responseData.get("items"));
        assertEquals(2, items.size());
        assertTrue(items.contains(toJsonObject(Map.of("productId", product1.getId(), "quantity", 30))));
        assertTrue(items.contains(toJsonObject(Map.of("productId", product2.getId(), "quantity", 10))));
    }

    @Test
    void testAmendUnknownProductReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/shoppingcart/amenditem")
                .content(toJsonString(Map.of("productId", "unknown product", "quantity", 30)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testAmendItemNotInShoppingCartReturnsBadRequest() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);

        mockMvc.perform(post("/shoppingcart/amenditem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 30)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testRemoveItemFromShoppingCart() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);

        // Add product 1, quantity 50
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 50)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Add product 2, quantity 10
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product2.getId(), "quantity", 10)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Remove product 1 item
        MvcResult result = mockMvc.perform(post("/shoppingcart/removeitem")
                .content(toJsonString(Map.of("productId", product1.getId())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Shopping cart items are as expected
        JsonNode responseData = getContentAsJson(result);
        List<JsonNode> items = Lists.newArrayList(responseData.get("items"));
        assertEquals(1, items.size());
        assertTrue(items.contains(toJsonObject(Map.of("productId", product2.getId(), "quantity", 10))));
    }

    @Test
    void testRemoveItemNotInShoppingCartReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/shoppingcart/removeitem")
                .content(toJsonString(Map.of("productId", "not-in-cart")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testProductIdIsMandatoryInRemoveItemRequest() throws Exception {
        mockMvc.perform(post("/shoppingcart/removeitem")
                .content(toJsonString(Map.of("other", "not-make-sense")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testCheckoutShoppingCart() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        // 50% off on second purchase of product 1
        PricePercentageDiscountDTO discount1 = createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on second purchase on product 1", 2, 50);
        // Buy product 2 get product 3 for free
        BundleProductsDiscountDTO discount2 = createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));

        // Add 50 product 1
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 50)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Add 20 product 2
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product2.getId(), "quantity", 20)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult getShoppingCartResult = mockMvc.perform(get("/shoppingcart"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode shoppingCartData = getContentAsJson(getShoppingCartResult);
        List<JsonNode> items = Lists.newArrayList(shoppingCartData.get("items"));
        assertEquals(2, items.size());

        // 50 product1s, 20 product2s in shopping cart
        assertTrue(items.contains(toJsonObject(Map.of("productId", product1.getId(), "quantity", 50))));
        assertTrue(items.contains(toJsonObject(Map.of("productId", product2.getId(), "quantity", 20))));

        // Checkout now
        MvcResult checkoutResult = mockMvc.perform(post("/shoppingcart/checkout"))
                .andExpect(status().isOk())
                .andReturn();

        OrderDTO order = getContentAsObject(checkoutResult, OrderDTO.class);
        assertEquals(190_000, order.getOriginalTotalPrice());
        assertEquals(137_500, order.getFinalTotalPrice());

        List<OrderItemDTO> orderItems = order.getItems();

        // Including product-1, product-2, product-3, discounted product-1
        assertEquals(4, orderItems.size());

        // non-discounted product1 is in original price (half of the total product 1 quantity)
        OrderItemDTO nonDiscountedProduct1Item = orderItems.stream().filter(orderItem -> orderItem.getProductId().equals(product1.getId()) && !orderItem.isDiscounted()).findFirst().get();
        assertFalse(nonDiscountedProduct1Item.isDiscounted());
        assertEquals(product1.getId(), nonDiscountedProduct1Item.getProductId());
        assertEquals(25_000, nonDiscountedProduct1Item.getOriginalPrice());
        assertEquals(25_000, nonDiscountedProduct1Item.getFinalPrice());
        assertEquals(25, nonDiscountedProduct1Item.getQuantity());

        // discounted product1 got 50% off (half of the total product 1 quantity)
        OrderItemDTO discountedProduct1Item = orderItems.stream().filter(orderItem -> orderItem.getProductId().equals(product1.getId()) && orderItem.isDiscounted()).findFirst().get();
        assertTrue(discountedProduct1Item.isDiscounted());
        assertEquals(product1.getId(), discountedProduct1Item.getProductId());
        assertEquals(25_000, discountedProduct1Item.getOriginalPrice());
        assertEquals(12_500, discountedProduct1Item.getFinalPrice());
        assertEquals(25, discountedProduct1Item.getQuantity());

        // product 2 is in original price
        OrderItemDTO product2Item = orderItems.stream().filter(orderItem -> orderItem.getProductId().equals(product2.getId())).findFirst().get();
        assertFalse(product2Item.isDiscounted());
        assertEquals(product2.getId(), product2Item.getProductId());
        assertEquals(100_000, product2Item.getOriginalPrice());
        assertEquals(100_000, product2Item.getFinalPrice());
        assertEquals(20, product2Item.getQuantity());

        // product 3 is for free
        OrderItemDTO product3Item = orderItems.stream().filter(orderItem -> orderItem.getProductId().equals(product3.getId())).findFirst().get();
        assertTrue(product3Item.isDiscounted());
        assertEquals(product3.getId(), product3Item.getProductId());
        assertEquals(40_000, product3Item.getOriginalPrice());
        assertEquals(0, product3Item.getFinalPrice());
        assertEquals(20, product3Item.getQuantity());

        // After checkout, shopping cart is empty
        MvcResult getShoppingCartResultAfterCheckout = mockMvc.perform(get("/shoppingcart"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseData = getContentAsJson(getShoppingCartResultAfterCheckout);

        assertFalse(responseData.isNull());
        assertTrue(responseData.get("items").isEmpty());
    }

    @Test
    void testCheckoutWhenProductsAreDeleted() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        // 50% off on second purchase of product 1
        PricePercentageDiscountDTO discount1 = createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on second purchase on product 1", 2, 50);
        // Buy product 2 get product 3 for free
        BundleProductsDiscountDTO discount2 = createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));

        // Add 50 product 1
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product1.getId(), "quantity", 50)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Add 20 product 2
        mockMvc.perform(post("/shoppingcart/additem")
                .content(toJsonString(Map.of("productId", product2.getId(), "quantity", 20)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Delete product 1
        deleteProduct(mockMvc, product1.getId());

        // Checkout, failed
        mockMvc.perform(post("/shoppingcart/checkout"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Remove product1
        mockMvc.perform(post("/shoppingcart/removeitem")
                .content(toJsonString(Map.of("productId", product1.getId())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Checkout again, succeed
        mockMvc.perform(post("/shoppingcart/checkout"))
                .andExpect(status().isOk())
                .andReturn();
    }


}