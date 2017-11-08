package pantrypals.models;

import java.util.Map;

/**
 * Created by Hunter Lee on 11/8/2017.
 */

public class Pantry {
    private Map<String, Item> items;
    private Map<String, Boolean> ownedBy;
    private Boolean shared;

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }

    public Map<String, Boolean> getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(Map<String, Boolean> ownedBy) {
        this.ownedBy = ownedBy;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public static class Item {
        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getExpiration() {
            return expiration;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        private double amount;
        private String expiration;
        private String name;
        private String unit;

        public Item() {}
    }
}
