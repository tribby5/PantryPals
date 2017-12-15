package pantrypals.pantry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;
import pantrypals.models.Pantry;
import pantrypals.models.User;

/**
 * Created by AlisonHuang on 12/11/17.
 */

public class DatabaseRetriever {
    private HashMap<String, Item> items;
    private PantryItemsAdapter itemsAdapter;
    private String pantryID;

    private HashMap<String, JointPantry> jPantries;
    private JointPantryAdapter jPantryAdapter;
    private String uid;

    public DatabaseRetriever() {

    }

    public HashMap<String, Item> retrievePantryItems(String pantryID, PantryItemsAdapter adapter) {
        this.pantryID = pantryID;
        this.itemsAdapter = adapter;
        items = new HashMap<>();
        final DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        FirebaseDatabase.getInstance().getReference().child("pantries").child(pantryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pantry pantry = dataSnapshot.getValue(Pantry.class);
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

    public HashMap<String, JointPantry> retrieveJointPantries(String myID, JointPantryAdapter adapter) {
        this.uid = myID;
        this.jPantryAdapter = adapter;
        this.jPantries = new HashMap<>();
        final DatabaseReference pantriesRef = FirebaseDatabase.getInstance().getReference().child("pantries");
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("userAccounts");
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getJointPantries() != null) {
                    for (final String jPantryID : user.getJointPantries().keySet()) {
                        pantriesRef.child(jPantryID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                JointPantry jPantry = dataSnapshot.getValue(JointPantry.class);
                                jPantry.setDatabaseId(jPantryID);
                                jPantries.put(jPantryID, jPantry);
                                jPantryAdapter.refresh(jPantries.values());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    jPantryAdapter.refresh(jPantries.values());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return jPantries;
    }
}
