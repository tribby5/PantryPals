package pantrypals.models;

/**
 * Created by mtribby on 12/12/17.
 */

public class GroceryItem {
    private double amount;
    private String name;
    private String unit;

    public GroceryItem() {}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
}
