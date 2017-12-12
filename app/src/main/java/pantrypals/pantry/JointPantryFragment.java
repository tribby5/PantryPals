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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;

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
        System.out.println("PRINT: joint pantryID = "+pantryID);
        items = new ArrayList<>();
        itemsAdapter = new PantryItemsAdapter(getActivity(), items);
        ItemsRetriever retriever = new ItemsRetriever(pantryID, itemsAdapter);
        items = retriever.retrievePantryItems();
        System.out.println("PRINT: ITEMS FROM THE RETRIEVER "+items.size());
        setupItemListView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupItemListView(View view){
        ListView itemListView = (ListView) view.findViewById(R.id.jointPantryItemsLV);
        itemListView.setAdapter(itemsAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //editModal(view);
            }
        });



    }


}
