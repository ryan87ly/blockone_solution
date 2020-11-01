package ry.an.dto;

import java.util.List;

public class ShoppingCartDTO {
    private List<ShoppingCartItemDTO> items;

    public List<ShoppingCartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ShoppingCartItemDTO> items) {
        this.items = items;
    }
}
