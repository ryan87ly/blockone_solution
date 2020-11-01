package ry.an.repository.product;

import ry.an.model.product.Product;

import java.util.Collection;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findProductById(String id);
    Collection<Product> allProducts();
    void save(Product product);
    void clear();
}
