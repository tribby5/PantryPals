package pantrypals.pantry;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;

/**
 * Created by AlisonHuang on 12/10/17.
 */

public class DummyDataGenerator {
    private DatabaseReference database;
    private String pantryPalsTable;
    private String itemsTable;
    private String pantriesTable;

    public DummyDataGenerator(String table, String items, String pantries) {
        database = FirebaseDatabase.getInstance().getReference();
        pantryPalsTable = table;
        itemsTable = items;
        pantriesTable = pantries;
    }

    public void generateDummyItemsData() {
        DatabaseReference itemsRecords = database.child(itemsTable).push();
        Item fish = new Item(itemsRecords.getKey());
        fish.setName("fish");
        fish.setAmount(8);
        fish.setUnit("lbs");
        fish.setExpiration("12/24/17");
        itemsRecords.setValue(fish);

        itemsRecords = database.child(itemsTable).push();
        Item salt = new Item(itemsRecords.getKey());
        salt.setName("salt");
        salt.setAmount(5);
        salt.setUnit("cups");
        salt.setExpiration("1/14/19");
        itemsRecords.setValue(salt);

        itemsRecords = database.child(itemsTable).push();
        Item beef = new Item(itemsRecords.getKey());
        beef.setName("beef");
        beef.setAmount(11);
        beef.setUnit("oz");
        beef.setExpiration("12/19/17");
        itemsRecords.setValue(beef);

        itemsRecords = database.child(itemsTable).push();
        Item sugar = new Item(itemsRecords.getKey());
        sugar.setName("sugar");
        sugar.setAmount(2);
        sugar.setUnit("bags");
        sugar.setExpiration("3/30/19");
        itemsRecords.setValue(sugar);

        DatabaseReference pantriesRecords = database.child(pantriesTable).push();
        JointPantry brownDorm = new JointPantry();
        brownDorm.setTitle("Brown Dorm");
        brownDorm.setShared(true);
        Map<String, Item> items = new HashMap<>();
        items.put("fish", fish);
        items.put("salt", salt);
        items.put("beef", beef);
        items.put("sugar", sugar);
        brownDorm.setItems(items);
        pantriesRecords.setValue(brownDorm);
    }
}
