package ry.an.model.order;

import ry.an.model.price.Price;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class Order {
    private static final DateTimeFormatter CREATION_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmSS");

    private final String id;
    private final List<CheckoutItemResult> items;
    private final ZonedDateTime creationTime;

    private Order(String id, List<CheckoutItemResult> items, ZonedDateTime creationTime) {
        this.id = id;
        this.items = items;
        this.creationTime = creationTime;
    }

    public static Order of(String id, List<CheckoutItemResult> items, ZonedDateTime creationTime) {
        return new Order(id, items, creationTime);
    }

    public String getId() {
        return id;
    }

    public List<CheckoutItemResult> getItems() {
        return items;
    }

    public Price getFinalTotalPrice() {
        return items.stream()
            .map(CheckoutItemResult::getFinalPrice)
            .reduce(Price.ZERO, Price::add);
    }

    public Price getOriginalTotalPrice() {
        return items.stream()
            .map(CheckoutItemResult::getOriginalPrice)
            .reduce(Price.ZERO, Price::add);
    }

    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public String getCreationTimeStr() {
        return creationTime.format(CREATION_TIME_FORMATTER);
    }
}
