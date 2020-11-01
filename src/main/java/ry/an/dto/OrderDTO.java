package ry.an.dto;

import java.util.List;

public class OrderDTO {
    private String id;
    private List<OrderItemDTO> items;
    private long originalTotalPrice;
    private long finalTotalPrice;
    private String creationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public long getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public void setOriginalTotalPrice(long originalTotalPrice) {
        this.originalTotalPrice = originalTotalPrice;
    }

    public long getFinalTotalPrice() {
        return finalTotalPrice;
    }

    public void setFinalTotalPrice(long finalTotalPrice) {
        this.finalTotalPrice = finalTotalPrice;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}
