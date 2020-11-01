# E-Shop system (Author Ryan)

## Description
The application is designed to make it easy to test. Immutability is the default behavior.
Test coverage is above `90%` as of now.

## Libraries/Frameworks
The application is built with JDK 11 + Gradle.

For application code:
- Springboot
- Guava

For testing
- JUnit5
- Mokito

For documentation
- springfox-swagger

## How to run
To run all the tests, please run
```
gradlew test
```

To start the application, please run
```
gradlew run
```

No database is integrated now. The application is using memory repositories for Java objects store. All data is gone when application restarted.

## Core features
After application is started, please visit http://localhost:8080/swagger-ui.html for API details.

Price unit is cent to avoid floating point accuracy issue.  
### Product
#### Model
```jsonc
{
    "id": "Product-1",
    "description": "Awesome product1",
    "price": 10000 // in cents
}
```

### Discount
#### Model
The application is supporting two discount types now
##### PRICE_PERCENTAGE_REDUCTION
```jsonc
{
  "description": "50% off for second purchase",
  "id": "Discount-1",
  "type": "PRICE_PERCENTAGE_REDUCTION", // Type value is fixed("PRICE_PERCENTAGE_REDUCTION")
  "discountOnPurchase": 2, // nth purchase can benefit from the discount
  "discountRate": 50, // % of price reduction
  "productId": "Product-1" // Product that can benefit from the discount
}
```
##### BUNDLE_PRODUCTS
```jsonc
{
    "description": "Buy Product-1, get a Product-2 for free",
    "id": "Discount-2",
    "type": "BUNDLE_PRODUCTS", // Type value is fixed("BUNDLE_PRODUCTS")
    "freeProducts": [ //  Free products
        "Product-2"
    ]
}
```
#### Shopping cart
To keep it simpler, we assume there is only one global shared shopping cart.
