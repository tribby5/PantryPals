package pantrypals.pantry;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;
import pantrypals.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class JointPantryFragment extends Fragment {

    private String masterTable = "pantry-pals";
    private String itemsTable = "items";
    private String pantriesTable = "pantries";
    private String pantryID;

    private DatabaseReference databaseRef;
    private DatabaseReference pantryRef;
    private PantryItemsAdapter itemsAdapter;
    private ArrayList<Item> items;
    private ChildEventListener itemsListener;

    public JointPantryFragment() {
        // Required empty public constructor
        DummyDataGenerator ddg = new DummyDataGenerator(masterTable, itemsTable, pantriesTable);
        //ddg.generateDummyItemsData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        System.out.println("PRINT: getting from bundle = "+pantryID);
        return inflater.inflate(R.layout.fragment_joint_pantry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pantryID = this.getArguments().getString("pantryID");
        System.out.println("PRINT: getting from bundle = "+pantryID);
        items = new ArrayList<>();
        itemsAdapter = new PantryItemsAdapter(getActivity(), items);
        retrievePantryItems();
        setupItemListView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupItemListView(View view){
        ListView itemListView = (ListView) view.findViewById(R.id.itemListView);
        itemListView.setAdapter(itemsAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //editModal(view);
            }
        });


//
//        Item fish = new Item();
//        fish.setName("fish");
//        fish.setAmount(8);
//        fish.setUnit("lbs");
//        fish.setExpiration("12/24/17");
//        items.add(fish);
//
//        Item salt = new Item();
//        salt.setName("salt");
//        salt.setAmount(5);
//        salt.setUnit("cups");
//        salt.setExpiration("1/14/19");
//        items.add(salt);
//
//        Item beef = new Item();
//        beef.setName("beef");
//        beef.setAmount(11);
//        beef.setUnit("oz");
//        beef.setExpiration("12/19/17");
//        items.add(beef);
//
//        Item sugar = new Item();
//        sugar.setName("sugar");
//        sugar.setAmount(2);
//        sugar.setUnit("bags");
//        sugar.setExpiration("3/30/19");
//        items.add(sugar);



    }

    private void retrievePantryItems() {

        DatabaseReference pantryRef = FirebaseDatabase.getInstance().getReference().child(pantriesTable);
        pantryRef = pantryRef.child(pantryID);
        pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // joint = new ArrayList<>();
                JointPantry pantry = dataSnapshot.getValue(JointPantry.class);
                System.out.println("PRINT: retrieving pantry items for "+pantry.getTitle());
                items.addAll(pantry.getItems().values());
                System.out.println("PRINT: items.size() = "+items.size());
                itemsAdapter.refresh(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
