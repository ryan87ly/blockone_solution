package ry.an.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ry.an.model.price.Price;
import ry.an.model.product.Product;
import ry.an.repository.product.ProductRepository;
import ry.an.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;

    @Autowired
    private ProductServiceImpl(ProductRepository productRepository, @Qualifier("productIdGenerator") IdGenerator idGenerator) {
        this.productRepository = productRepository;
        this.idGenerator = idGenerator;
    }

    public static ProductServiceImpl of(ProductRepository productRepository, IdGenerator idGenerator) {
        return new ProductServiceImpl(productRepository, idGenerator);
    }

    @Override
    public Optional<Product> findActiveProductById(String id) {
        return productRepository.findProductById(id)
                .filter(Product::isActive);
    }

    @Override
    public Optional<Product> findProductById(String id) {
        return productRepository.findProductById(id);
    }

    @Override
    public List<Product> allActiveProducts() {
        return productRepository.allProducts()
                .stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public Product createProduct(String productDescription, Price price) {
        String productId = idGenerator.nextId();
        Product product = Product.of(productId, productDescription, price, true);
        productRepository.save(product);
        return product;
    }

    @Override
    public Product updateProduct(String id, String productDescription, Price price) {
        Product product = Product.of(id, productDescription, price, true);
        productRepository.save(product);
        return product;
    }

    @Override
    public void deactivateProduct(String id) {
        Optional<Product> productOpt = productRepository.findProductById(id);
        if (productOpt.isEmpty() || !productOpt.get().isActive()) {
            return;
        }
        Product updatedProduct = productOpt.get().withActive(false);
        productRepository.save(updatedProduct);
    }

}
