package pantrypals.models;

/**
 * Created by AlisonHuang on 12/10/17.
 */
public class Item {
    private String databaseID;
    private double amount;
    private String expiration;
    private String name;
    private String unit;

    public Item() {}

    public Item(String databaseID) {
        this.databaseID = databaseID;
    }

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

    public String getDatabaseId() {
        return this.databaseID;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseID = databaseId;
    }
}
