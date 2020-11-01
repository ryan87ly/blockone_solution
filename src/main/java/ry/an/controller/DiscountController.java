package ry.an.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ry.an.dto.BundleProductsDiscountDTO;
import ry.an.dto.DiscountDTOBase;
import ry.an.dto.PricePercentageDiscountDTO;
import ry.an.model.discount.BundleProductsDiscount;
import ry.an.model.discount.DiscountEntity;
import ry.an.model.discount.DiscountRate;
import ry.an.model.discount.PricePercentageDiscount;
import ry.an.service.discount.DiscountService;
import ry.an.service.product.ProductService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/discount")
public class DiscountController {

    @Autowired
    private ProductService productService;

    @Autowired
    private DiscountService discountService;

    @GetMapping
    @ResponseBody
    public List<DiscountDTOBase> getDiscounts() {
        return discountService.allActiveDiscounts()
                .stream()
                .map(this::discountToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public DiscountDTOBase createDiscount(@RequestBody DiscountDTOBase baseDiscountDTO) {
        DiscountEntity discountRequestEntity = validateAndConvertDTOToDiscount(baseDiscountDTO);
        DiscountEntity updatedDiscount = discountService.createDiscount(discountRequestEntity);
        return discountToDTO(updatedDiscount);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public DiscountDTOBase getDiscount(@PathVariable("id") String id) {
        Optional<DiscountEntity> discountOpt = discountService.findActiveDiscountById(id);
        if (discountOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found, id=" + id);
        }
        return discountToDTO(discountOpt.get());
    }

    @PutMapping(value = "/{id}")
    @ResponseBody
    public DiscountDTOBase updateDiscount(@PathVariable("id") String id, @RequestBody DiscountDTOBase baseDiscountDTO) {
        Optional<DiscountEntity> discountOpt = discountService.findActiveDiscountById(id);
        if (discountOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found, id=" + id);
        }
        DiscountEntity discount = discountService.updateDiscount(id, validateAndConvertDTOToDiscount(baseDiscountDTO));
        return discountToDTO(discount);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteDiscount(@PathVariable("id") String id) {
        Optional<DiscountEntity> discountOpt = discountService.findActiveDiscountById(id);
        if (discountOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found, id=" + id);
        }
        discountService.deactivateDiscount(id);
    }

    private DiscountEntity validateAndConvertDTOToDiscount(DiscountDTOBase baseDiscountDTO) {
        InputValidations.requireNotNullOrEmpty(baseDiscountDTO.getDescription(), () -> "description is mandatory");

        if (baseDiscountDTO instanceof BundleProductsDiscountDTO) {
            return validateAndConvertBundleProductDiscount((BundleProductsDiscountDTO) baseDiscountDTO);
        } else if (baseDiscountDTO instanceof PricePercentageDiscountDTO) {
            return validateAndConvertPricePercentageDiscount((PricePercentageDiscountDTO) baseDiscountDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported Discount Type: " + baseDiscountDTO.getType());
        }
    }

    private BundleProductsDiscount validateAndConvertBundleProductDiscount(BundleProductsDiscountDTO discountRequestDTO) {
        InputValidations.requireNotNullOrEmpty(discountRequestDTO.getProductId(), () -> "productId is mandatory");
        InputValidations.requirePresent(productService.findActiveProductById(discountRequestDTO.getProductId()), () -> "Product not found, id=" + discountRequestDTO.getProductId());
        InputValidations.requireNotNullOrEmpty(discountRequestDTO.getFreeProducts(), () -> "freeProducts is mandatogy");

        for (String freeProduct : discountRequestDTO.getFreeProducts()) {
            InputValidations.requireNotNullOrEmpty(freeProduct, () -> "productId in freeProducts is mandatory");
            InputValidations.requirePresent(productService.findActiveProductById(freeProduct), () -> "Product not found, id=" + freeProduct);
        }

        return BundleProductsDiscount.of(null, discountRequestDTO.getDescription(), true, discountRequestDTO.getProductId(), discountRequestDTO.getFreeProducts());
    }

    private PricePercentageDiscount validateAndConvertPricePercentageDiscount(PricePercentageDiscountDTO discountRequestDTO) {
        InputValidations.requireNotNullOrEmpty(discountRequestDTO.getProductId(), () -> "productId is mandatory");
        InputValidations.requirePresent(productService.findActiveProductById(discountRequestDTO.getProductId()), () -> "Product not found, id=" + discountRequestDTO.getProductId());
        InputValidations.require(discountRequestDTO.getDiscountOnPurchase() >= 1, () -> "discountOnPurchase should larger or equal than 1");
        InputValidations.require(discountRequestDTO.getDiscountRate() > 0 && discountRequestDTO.getDiscountRate() < 100, () -> "discountRate should between 1 and 99");

        DiscountRate discountRate = DiscountRate.fromPercentage(discountRequestDTO.getDiscountRate());
        return PricePercentageDiscount.of(null, discountRequestDTO.getDescription(), true, discountRequestDTO.getProductId(), discountRequestDTO.getDiscountOnPurchase(), discountRate);
    }

    private DiscountDTOBase discountToDTO(DiscountEntity discount) {
        if (discount instanceof PricePercentageDiscount) {
            return toPricePercentageDiscountDTO((PricePercentageDiscount) discount);
        } else if (discount instanceof BundleProductsDiscount) {
            return toBundleProductsDiscountDTO((BundleProductsDiscount) discount);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported Discount Type: " + discount.getProductDiscountType().name());
        }
    }

    private PricePercentageDiscountDTO toPricePercentageDiscountDTO(PricePercentageDiscount discount) {
        PricePercentageDiscountDTO dto = new PricePercentageDiscountDTO();
        dto.setId(discount.getId());
        dto.setDescription(discount.getDescription());
        dto.setType(discount.getProductDiscountType().name());
        dto.setDiscountOnPurchase(discount.getDiscountOnPurchase());
        dto.setProductId(discount.getProductId());
        dto.setDiscountRate(discount.getDiscountRate().getPercentageValue());
        return dto;
    }

    private BundleProductsDiscountDTO toBundleProductsDiscountDTO(BundleProductsDiscount discount) {
        BundleProductsDiscountDTO dto = new BundleProductsDiscountDTO();
        dto.setId(discount.getId());
        dto.setDescription(discount.getDescription());
        dto.setType(discount.getProductDiscountType().name());
        dto.setProductId(discount.getProductId());
        dto.setFreeProducts(discount.getFreeProductIds());
        dto.setType(discount.getProductDiscountType().name());
        return dto;
    }
}
