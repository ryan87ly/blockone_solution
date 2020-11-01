package ry.an.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = BundleProductsDiscountDTO.class, name = "BUNDLE_PRODUCTS"),
  @JsonSubTypes.Type(value = PricePercentageDiscountDTO.class, name = "PRICE_PERCENTAGE_REDUCTION")
})
public abstract class DiscountDTOBase {
    private String id;
    private String type;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
