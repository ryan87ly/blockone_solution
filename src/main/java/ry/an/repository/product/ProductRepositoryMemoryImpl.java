package ry.an.repository.product;

import ry.an.model.product.Product;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JVM memory ProductRepository implementation
 */
public class ProductRepositoryMemoryImpl implements ProductRepository {
    private final ConcurrentMap<String, Product> productMap;

    private ProductRepositoryMemoryImpl(ConcurrentMap<String, Product> productMap) {
        this.productMap = productMap;
    }

    public static ProductRepositoryMemoryImpl empty() {
        return new ProductRepositoryMemoryImpl(new ConcurrentHashMap<>());
    }

    @Override
    public Optional<Product> findProductById(String id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public Collection<Product> allProducts() {
        return productMap.values();
    }

    @Override
    public void save(Product product) {
        productMap.put(product.getId(), product);
    }

    @Override
    public void clear() {
        productMap.clear();
    }
}
