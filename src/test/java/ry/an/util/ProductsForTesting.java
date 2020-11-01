package ry.an.util;

import ry.an.model.price.Price;
import ry.an.model.product.Product;
import ry.an.service.product.ProductService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ProductsForTesting {
    public static final Product testProduct1 = Product.of("P-1", "Product 1", Price.fromDollar(BigDecimal.valueOf(201)), true);
    public static final Product testProduct2 = Product.of("P-2", "Product 2", Price.fromDollar(BigDecimal.valueOf(52)), true);
    public static final Product testProduct3 = Product.of("P-3", "Product 3", Price.fromDollar(BigDecimal.valueOf(100)), true);
    public static final Product testProduct4 = Product.of("P-4", "Product 4", Price.fromDollar(BigDecimal.valueOf(150)), false);

    public static ProductService mockedProductService() {
        ProductService productService = mock(ProductService.class);

        when(productService.findActiveProductById(anyString())).thenReturn(Optional.empty());
        when(productService.findActiveProductById(eq(testProduct1.getId()))).thenReturn(Optional.of(testProduct1));
        when(productService.findActiveProductById(eq(testProduct2.getId()))).thenReturn(Optional.of(testProduct2));
        when(productService.findActiveProductById(eq(testProduct3.getId()))).thenReturn(Optional.of(testProduct3));

        when(productService.findProductById(eq(testProduct1.getId()))).thenReturn(Optional.of(testProduct1));
        when(productService.findProductById(eq(testProduct2.getId()))).thenReturn(Optional.of(testProduct2));
        when(productService.findProductById(eq(testProduct3.getId()))).thenReturn(Optional.of(testProduct3));
        when(productService.findProductById(eq(testProduct4.getId()))).thenReturn(Optional.of(testProduct4));

        return productService;
    }
}
