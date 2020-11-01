package ry.an.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ry.an.dto.BundleProductsDiscountDTO;
import ry.an.dto.DiscountDTOBase;
import ry.an.dto.PricePercentageDiscountDTO;
import ry.an.dto.ProductDTO;
import ry.an.model.discount.DiscountType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ry.an.controller.ControllerTestUtil.*;
import static ry.an.util.JsonUtil.toJsonObject;
import static ry.an.util.JsonUtil.toJsonString;
import static ry.an.util.MvcResultUtil.getContentAsJson;
import static ry.an.util.MvcResultUtil.getContentAsObject;

@SpringBootTest
@AutoConfigureMockMvc
public class DiscountControllerTest extends ControllerTestBase {

    @Test
    void testDiscountCanBeCreated() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on product1", 1, 50);
        createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));
    }

    @Test
    void testThatBadRequestIsReturnWhenDiscountIsCreated() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);

        // Verify bad request is return when creating price percentage discount for unknown product
        createPricePercentageDiscountRequest(mockMvc, "unknown-product", "50% off", 1, 50)
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Verify bad request is return when creating bundle product discount for unknown product
        createBundleProductDiscountRequest(mockMvc, "unknown-product2", "50% off", List.of(product1.getId(), product2.getId()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Verify bad request is return when unknown product is free when creating bundle discount
        createBundleProductDiscountRequest(mockMvc, product2.getId(), "50% off", List.of("unknown-product"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    void testGetDiscountById() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        PricePercentageDiscountDTO discount1 = createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on product1", 1, 50);
        BundleProductsDiscountDTO discount2 = createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));

        for (DiscountDTOBase discountDTO : List.of(discount1, discount2)) {
            MvcResult getProductResult = mockMvc.perform(get(String.format("/discount/%s", discountDTO.getId())))
                .andExpect(status().isOk())
                .andReturn();
            assertEquals(toJsonObject(discountDTO), getContentAsJson(getProductResult));
        }

         // For the non existed discount, returns 404
        mockMvc.perform(get("/discount/unknown"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testGetAllDiscounts() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        PricePercentageDiscountDTO discount1 = createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on product1", 1, 50);
        BundleProductsDiscountDTO discount2 = createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));

        MvcResult allDiscountsResult = mockMvc.perform(get("/discount"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode allDiscounts = getContentAsJson(allDiscountsResult);

        assertTrue(allDiscounts.isArray());

        List<JsonNode> productList = Lists.newArrayList(allDiscounts);
        assertTrue(productList.stream().anyMatch(product -> product.equals(toJsonObject(discount1))));
        assertTrue(productList.stream().anyMatch(product -> product.equals(toJsonObject(discount2))));
    }

    @Test
    void testUpdateDiscount() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        PricePercentageDiscountDTO discount1 = createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on product1", 1, 50);
        BundleProductsDiscountDTO discount2 = createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));

        // Perform update on discount1
        mockMvc.perform(put(String.format("/discount/%s", discount1.getId()))
                .content(toJsonString(Map.of("productId", discount1.getProductId(), "description", "60% off now!", "discountOnPurchase", 1, "discountRate", 60, "type", DiscountType.PRICE_PERCENTAGE_REDUCTION.name())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify update on discount1 is done
        MvcResult discount1Result = mockMvc.perform(get(String.format("/discount/%s", discount1.getId())))
                .andExpect(status().isOk())
                .andReturn();
        PricePercentageDiscountDTO discount1DTO = getContentAsObject(discount1Result, PricePercentageDiscountDTO.class);
        assertEquals(discount1.getId(), discount1DTO.getId());
        assertEquals("60% off now!", discount1DTO.getDescription());
        assertEquals(1, discount1DTO.getDiscountOnPurchase());
        assertEquals(60, discount1DTO.getDiscountRate());

        // Perform update on discount2
        mockMvc.perform(put(String.format("/discount/%s", discount2.getId()))
                .content(toJsonString(Map.of("productId", discount2.getProductId(), "description", "Get product 1 for free now!", "freeProducts", List.of(product1.getId()), "type", DiscountType.BUNDLE_PRODUCTS.name())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify update on discount2 is done
        MvcResult discount2Result = mockMvc.perform(get(String.format("/discount/%s", discount2.getId())))
                .andExpect(status().isOk())
                .andReturn();
        BundleProductsDiscountDTO discount2DTO = getContentAsObject(discount2Result, BundleProductsDiscountDTO.class);
        assertEquals(discount2.getId(), discount2DTO.getId());
        assertEquals("Get product 1 for free now!", discount2DTO.getDescription());
        assertEquals(List.of(product1.getId()), discount2DTO.getFreeProducts());

        // Perform update on unknown discount
        mockMvc.perform(put(String.format("/discount/%s", "unknown-discount"))
                .content(toJsonString(Map.of("productId", discount2.getProductId(), "description", "Get product 1 for free now!", "freeProducts", List.of(product1.getId()), "type", DiscountType.BUNDLE_PRODUCTS.name())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testDeleteDiscount() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        PricePercentageDiscountDTO discount1 = createPricePercentageDiscount(mockMvc, product1.getId(), "50% off on product1", 1, 50);
        BundleProductsDiscountDTO discount2 = createBundleProductDiscount(mockMvc, product2.getId(), "Buy product 2 get product 3 for free", List.of(product3.getId()));

        // Discount2 can be found after creation
        mockMvc.perform(get(String.format("/discount/%s", discount2.getId())))
                .andExpect(status().isOk())
                .andReturn();

        // Perform delete on discount2
        mockMvc.perform(delete(String.format("/discount/%s", discount2.getId())))
                .andExpect(status().isOk());

        // Verify discount2 is not able to find
        mockMvc.perform(get(String.format("/discount/%s", discount2.getId())))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        // Perform delete on unknown discount, return not found
        mockMvc.perform(delete(String.format("/discount/%s", "unknown")))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }
}
