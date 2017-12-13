package pantrypals.pantry;

import com.android.databaes.pantrypals.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;

/**
 * Created by AlisonHuang on 12/11/17.
 */

public class ItemsRetriever {
    private HashMap<String, Item> items;
    private PantryItemsAdapter itemsAdapter;
    private String pantryID;

    public ItemsRetriever() {

    }

    public HashMap<String, Item> retrievePantryItems(String pantryID, PantryItemsAdapter adapter) {
        this.pantryID = pantryID;
        this.itemsAdapter = adapter;
        items = new HashMap<>();
        final DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        FirebaseDatabase.getInstance().getReference().child("pantries").child(pantryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JointPantry pantry = dataSnapshot.getValue(JointPantry.class);
                if (pantry.getItems() != null) {
                    for (final String itemID : pantry.getItems().keySet()) {
                        itemsRef.child(itemID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Item item = dataSnapshot.getValue(Item.class);
                                item.setDatabaseId(itemID);
                                items.put(itemID, item);
                                itemsAdapter.refresh(items.values());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    itemsAdapter.refresh(items.values());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return items;
    }
}
