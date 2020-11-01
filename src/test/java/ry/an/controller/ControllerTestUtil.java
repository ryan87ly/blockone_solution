package ry.an.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ry.an.dto.BundleProductsDiscountDTO;
import ry.an.dto.PricePercentageDiscountDTO;
import ry.an.dto.ProductDTO;
import ry.an.model.discount.DiscountType;
import ry.an.model.product.Product;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ry.an.util.JsonUtil.toJsonString;
import static ry.an.util.MvcResultUtil.getContentAsObject;

public final class ControllerTestUtil {

    public static ProductDTO createProduct(MockMvc mockMvc, Product product) throws Exception {
        return createProduct(mockMvc, product.getDescription(), product.getPrice().getValueInCents());
    }

    public static ProductDTO createProduct(MockMvc mockMvc, String description, long price) throws Exception {
        MvcResult result = mockMvc.perform(post("/product")
                .content(toJsonString(Map.of("description", description, "price", price)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        ProductDTO contentObject = getContentAsObject(result, ProductDTO.class);
        assertEquals(description, contentObject.getDescription());
        assertEquals(price, contentObject.getPrice());
        return contentObject;
    }

    public static void deleteProduct(MockMvc mockMvc, String productId) throws Exception {
        mockMvc.perform(delete(String.format("/product/%s", productId)))
                .andExpect(status().isOk());
    }

    public static PricePercentageDiscountDTO createPricePercentageDiscount(MockMvc mockMvc, String productId, String description, int discountOnPurchase, int discountRate) throws Exception {
        MvcResult result = createPricePercentageDiscountRequest(mockMvc, productId, description, discountOnPurchase, discountRate)
                .andExpect(status().isCreated())
                .andReturn();
        PricePercentageDiscountDTO contentObject = getContentAsObject(result, PricePercentageDiscountDTO.class);
        assertEquals(description, contentObject.getDescription());
        assertEquals(productId, contentObject.getProductId());
        assertEquals(discountOnPurchase, contentObject.getDiscountOnPurchase());
        assertEquals(discountRate, contentObject.getDiscountRate());
        assertEquals(DiscountType.PRICE_PERCENTAGE_REDUCTION.name(), contentObject.getType());
        return contentObject;
    }

    public static ResultActions createPricePercentageDiscountRequest(MockMvc mockMvc, String productId, String description, int discountOnPurchase, int discountRate) throws Exception {
        return mockMvc.perform(post("/discount")
                .content(toJsonString(
                    Map.of(
                        "description", description,
                        "productId", productId,
                        "discountOnPurchase", discountOnPurchase,
                        "discountRate", discountRate,
                        "type", DiscountType.PRICE_PERCENTAGE_REDUCTION.name()
                    )
                ))
                .contentType(MediaType.APPLICATION_JSON));
    }

    public static BundleProductsDiscountDTO createBundleProductDiscount(MockMvc mockMvc, String productId, String description, List<String> freeProducts) throws Exception {
        MvcResult result = createBundleProductDiscountRequest(mockMvc, productId, description, freeProducts)
                .andExpect(status().isCreated())
                .andReturn();
        BundleProductsDiscountDTO contentObject = getContentAsObject(result, BundleProductsDiscountDTO.class);
        assertEquals(description, contentObject.getDescription());
        assertEquals(productId, contentObject.getProductId());
        assertEquals(DiscountType.BUNDLE_PRODUCTS.name(), contentObject.getType());
        assertEquals(freeProducts, contentObject.getFreeProducts());
        return contentObject;
    }

    public static ResultActions createBundleProductDiscountRequest(MockMvc mockMvc, String productId, String description, List<String> freeProducts) throws Exception {
        return mockMvc.perform(post("/discount")
                .content(toJsonString(
                    Map.of(
                        "description", description,
                        "productId", productId,
                        "freeProducts", freeProducts,
                        "type", DiscountType.BUNDLE_PRODUCTS.name()
                    )
                ))
                .contentType(MediaType.APPLICATION_JSON));
    }
}
