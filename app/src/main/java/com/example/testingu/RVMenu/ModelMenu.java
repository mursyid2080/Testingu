package com.example.testingu.RVMenu;

public class ModelMenu {
    String menuId;
    String menuName;
    String price;

    public ModelMenu() {
    }

    public ModelMenu(String menuId, String menuName, String price) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String id) {
        this.menuId = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String name) {
        this.menuName = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
