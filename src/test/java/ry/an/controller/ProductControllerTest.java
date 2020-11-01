package ry.an.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ry.an.dto.ProductDTO;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ry.an.controller.ControllerTestUtil.createProduct;
import static ry.an.util.JsonUtil.toJsonObject;
import static ry.an.util.JsonUtil.toJsonString;
import static ry.an.util.MvcResultUtil.getContentAsJson;
import static ry.an.util.MvcResultUtil.getContentAsObject;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest extends ControllerTestBase {

	@Test
    void testProductCanBeCreated() throws Exception {
        createProduct(mockMvc, "AwesomeProduct1", 1000);
        createProduct(mockMvc, "AwesomeProduct2", 5000);
        createProduct(mockMvc, "AwesomeProduct3", 2000);
    }

    @Test
    void testGetAllProducts() throws Exception {
	    ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

	    MvcResult allProductsResult = mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode allProducts = getContentAsJson(allProductsResult);

        assertTrue(allProducts.isArray());

        List<JsonNode> productList = Lists.newArrayList(allProducts);
        assertTrue(productList.stream().anyMatch(product -> product.equals(toJsonObject(product1))));
        assertTrue(productList.stream().anyMatch(product -> product.equals(toJsonObject(product2))));
        assertTrue(productList.stream().anyMatch(product -> product.equals(toJsonObject(product3))));
    }

    @Test
    void testGetProductById() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        // Verify that all products can be fetched
        for (ProductDTO productDTO : List.of(product1, product2, product3)) {
            MvcResult getProductResult = mockMvc.perform(get(String.format("/product/%s", productDTO.getId())))
                .andExpect(status().isOk())
                .andReturn();
            assertEquals(toJsonObject(productDTO), getContentAsJson(getProductResult));
        }

        // For the non existed product, returns 404
        mockMvc.perform(get("/product/unknown"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        // Perform update on product2
        mockMvc.perform(put(String.format("/product/%s", product2.getId()))
                .content(toJsonString(Map.of("description", "Super AwesomeProduct2", "price", 10000L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify update on product2 is done
        MvcResult product2Result = mockMvc.perform(get(String.format("/product/%s", product2.getId())))
                .andExpect(status().isOk())
                .andReturn();
        ProductDTO product2DTO = getContentAsObject(product2Result, ProductDTO.class);
        assertEquals(product2.getId(), product2DTO.getId());
        assertEquals("Super AwesomeProduct2", product2DTO.getDescription());
        assertEquals(10000L, product2DTO.getPrice());

        // Perform update on unknown product
        mockMvc.perform(put(String.format("/product/%s", "known-product"))
                .content(toJsonString(Map.of("description", "unknown description", "price", 10000L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testDeleteProduct() throws Exception {
        ProductDTO product1 = createProduct(mockMvc, "AwesomeProduct1", 1000);
        ProductDTO product2 = createProduct(mockMvc, "AwesomeProduct2", 5000);
        ProductDTO product3 = createProduct(mockMvc, "AwesomeProduct3", 2000);

        // Product 3 can be found after creation
        mockMvc.perform(get(String.format("/product/%s", product3.getId())))
                .andExpect(status().isOk())
                .andReturn();

        // Perform delete on product3
        mockMvc.perform(delete(String.format("/product/%s", product3.getId())))
                .andExpect(status().isOk());

        // Verify product3 is not able to find
        mockMvc.perform(get(String.format("/product/%s", product3.getId())))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        // Perform delete on unknown product, return not found
        mockMvc.perform(delete(String.format("/product/%s", "unknown")))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

}