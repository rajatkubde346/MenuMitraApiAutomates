package com.menumitra.apiRequest;

import java.util.List;

public class MenuRequest 
{
    private Integer outlet_id;
    private String user_id;
    private String menu_id;
    private String menu_cat_id;
    private String name;
    private String food_type;
    private String description;
    private String spicy_index;
    private List<PortionData> portion_data;
    private String images;
    private String ingredients;
    private String offer;
    private String rating;
    private String app_source;
    
    public static class PortionData {
        private String portion_name;
        private double price;
        private String unit_value;
        private String unit_type;
        private int flag;

        public String getPortion_name() {
            return portion_name;
        }

        public void setPortion_name(String portion_name) {
            this.portion_name = portion_name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getUnit_value() {
            return unit_value;
        }

        public void setUnit_value(String unit_value) {
            this.unit_value = unit_value;
        }

        public String getUnit_type() {
            return unit_type;
        }

        public void setUnit_type(String unit_type) {
            this.unit_type = unit_type;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }
    }
    
    public Integer getOutlet_id() {
        return outlet_id;
    }
    
    public void setOutlet_id(Integer outlet_id) {
        this.outlet_id = outlet_id;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
    public String getMenu_id() {
        return menu_id;
    }
    
    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }
    
    public String getMenu_cat_id() {
        return menu_cat_id;
    }
    
    public void setMenu_cat_id(String menu_cat_id) {
        this.menu_cat_id = menu_cat_id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFood_type() {
        return food_type;
    }
    
    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSpicy_index() {
        return spicy_index;
    }
    
    public void setSpicy_index(String spicy_index) {
        this.spicy_index = spicy_index;
    }
    
    public List<PortionData> getPortion_data() {
        return portion_data;
    }
    
    public void setPortion_data(List<PortionData> portion_data) {
        this.portion_data = portion_data;
    }
    
    public String getImages() {
        return images;
    }
    
    public void setImages(String images) {
        this.images = images;
    }
    
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getOffer() {
        return offer;
    }
    
    public void setOffer(String offer) {
        this.offer = offer;
    }
    
    public String getRating() {
        return rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public String getApp_source() {
        return app_source;
    }
    
    public void setApp_source(String app_source) {
        this.app_source = app_source;
    }
}
