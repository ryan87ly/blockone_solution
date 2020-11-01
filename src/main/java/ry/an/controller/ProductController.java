package ry.an.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ry.an.dto.ProductDTO;
import ry.an.model.price.Price;
import ry.an.model.product.Product;
import ry.an.service.product.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @ResponseBody
    public List<ProductDTO> getProducts() {
        return productService.allActiveProducts()
                .stream()
                .map(this::convertProductToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        InputValidations.requireNotNullOrEmpty(productDTO.getDescription(), () -> "description is mandatory");
        InputValidations.require(productDTO.getPrice() > 0, () -> "price should be positive");

        String productDescription = productDTO.getDescription();
        long priceInCents = productDTO.getPrice();
        Price price = Price.fromCents(priceInCents);
        Product product = productService.createProduct(productDescription, price);
        return convertProductToDTO(product);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ProductDTO getProduct(@PathVariable("id") String id) {
        Optional<Product> productOpt = productService.findActiveProductById(id);
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found, id=" + id);
        }
        return convertProductToDTO(productOpt.get());
    }

    @PutMapping(value = "/{id}")
    @ResponseBody
    public ProductDTO updateProduct(@PathVariable("id") String id, @RequestBody ProductDTO productDTO) {
        InputValidations.requireNotNullOrEmpty(productDTO.getDescription(), () -> "description is mandatory");
        InputValidations.require(productDTO.getPrice() > 0, () -> "price should be positive");

        Optional<Product> productOpt = productService.findActiveProductById(id);
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found, id=" + id);
        }
        Product product = productService.updateProduct(id, productDTO.getDescription(), Price.fromCents(productDTO.getPrice()));
        return convertProductToDTO(product);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteProduct(@PathVariable("id") String id) {
        Optional<Product> productOpt = productService.findActiveProductById(id);
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found, id=" + id);
        }
        productService.deactivateProduct(id);
    }

    private ProductDTO convertProductToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice().getValueInCents());
        return productDTO;
    }



}
