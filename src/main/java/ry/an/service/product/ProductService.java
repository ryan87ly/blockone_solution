package ry.an.service.product;

import ry.an.model.price.Price;
import ry.an.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findActiveProductById(String id);
    Optional<Product> findProductById(String id);
    List<Product> allActiveProducts();
    Product createProduct(String productDescription, Price price);
    Product updateProduct(String id, String productDescription, Price price);
    void deactivateProduct(String id);
}
