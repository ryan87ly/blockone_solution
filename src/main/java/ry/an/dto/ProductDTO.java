package ry.an.dto;

import javax.validation.constraints.NotBlank;

public class ProductDTO {
    private String id;

    @NotBlank(message = "Description is mandatory")
    private String description;

    private long price; // In cents

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
