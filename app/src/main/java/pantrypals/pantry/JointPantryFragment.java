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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import pantrypals.models.Item;

/**
 * A simple {@link Fragment} subclass.
 */
public class JointPantryFragment extends Fragment {

    private String table = "pantry-pals";
    private String itemsTable = "items";
    private String pantriesTable = "pantries";

    public JointPantryFragment() {
        // Required empty public constructor
        DummyDataGenerator ddg = new DummyDataGenerator(table, itemsTable, pantriesTable);
        //ddg.generateDummyItemsData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_joint_pantry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        setupItemListView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupItemListView(View view){
        ListView itemListView = (ListView) view.findViewById(R.id.itemListView);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //editModal(view);
            }
        });



        ArrayList<Item> items = new ArrayList<>();

        Item fish = new Item();
        fish.setName("fish");
        fish.setAmount(8);
        fish.setUnit("lbs");
        fish.setExpiration("12/24/17");
        items.add(fish);

        Item salt = new Item();
        salt.setName("salt");
        salt.setAmount(5);
        salt.setUnit("cups");
        salt.setExpiration("1/14/19");
        items.add(salt);

        Item beef = new Item();
        beef.setName("beef");
        beef.setAmount(11);
        beef.setUnit("oz");
        beef.setExpiration("12/19/17");
        items.add(beef);

        Item sugar = new Item();
        sugar.setName("sugar");
        sugar.setAmount(2);
        sugar.setUnit("bags");
        sugar.setExpiration("3/30/19");
        items.add(sugar);



        PantryItemsAdapter adapter = new PantryItemsAdapter(getActivity(), items);

        itemListView.setAdapter(adapter);
    }

    private void getPantryItems(String pantryID, ArrayList<Item> items) {

    }
}
