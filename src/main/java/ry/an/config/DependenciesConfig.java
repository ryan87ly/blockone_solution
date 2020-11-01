package ry.an.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ry.an.core.pricing.OriginalPriceStrategy;
import ry.an.core.pricing.PricingStrategy;
import ry.an.repository.discount.DiscountRepository;
import ry.an.repository.discount.DiscountRepositoryMemoryImpl;
import ry.an.repository.product.ProductRepository;
import ry.an.repository.product.ProductRepositoryMemoryImpl;
import ry.an.repository.shoppingcart.ShoppingCartRepository;
import ry.an.repository.shoppingcart.ShoppingCartRepositoryMemoryImpl;
import ry.an.util.IdGenerator;
import ry.an.util.PrefixedCounterIdGenerator;

import java.time.Clock;
import java.time.ZoneOffset;

@Configuration
@ComponentScan("ry.an")
public class DependenciesConfig {

    @Bean
    public ProductRepository productRepository() {
        return ProductRepositoryMemoryImpl.empty();
    }

    @Bean(name = "productIdGenerator")
    public IdGenerator productIdGenerator() {
        return PrefixedCounterIdGenerator.of("Product");
    }

    @Bean
    public DiscountRepository discountRepository() {
        return DiscountRepositoryMemoryImpl.empty();
    }

    @Bean(name = "discountIdGenerator")
    public IdGenerator discountIdGenerator() {
        return PrefixedCounterIdGenerator.of("Discount");
    }

    @Bean
    public ShoppingCartRepository shoppingCartRepository() {
        return ShoppingCartRepositoryMemoryImpl.empty();
    }

    @Bean(name = "orderIdGenerator")
    public IdGenerator getOrderIdGenerator() {
        return PrefixedCounterIdGenerator.of("Order");
    }

    @Bean
    public Clock getClock() {
        return Clock.system(ZoneOffset.ofHours(8));
    }

    @Bean
    public PricingStrategy getDefaultPricingStrategy() {
        return OriginalPriceStrategy.INSTANCE;
    }
}
