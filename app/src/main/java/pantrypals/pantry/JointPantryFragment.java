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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class JointPantryFragment extends Fragment {


    public JointPantryFragment() {
        // Required empty public constructor
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
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

        //TODO remove dummy data
        HashMap<String, String> testItem = new HashMap<>();
        testItem.put(PersonalPantryFragment.FIRST_COLUMN, "chicken");
        testItem.put(PersonalPantryFragment.SECOND_COLUMN, "6");
        testItem.put(PersonalPantryFragment.THIRD_COLUMN, "lb");
        testItem.put(PersonalPantryFragment.FOURTH_COLUMN, "11/18/17");

        HashMap<String, String> testItem2 = new HashMap<>();
        testItem2.put(PersonalPantryFragment.FIRST_COLUMN, "butter");
        testItem2.put(PersonalPantryFragment.SECOND_COLUMN, "2");
        testItem2.put(PersonalPantryFragment.THIRD_COLUMN, "tbsp");
        testItem2.put(PersonalPantryFragment.FOURTH_COLUMN, "11/10/17");


        items.add(testItem);
        items.add(testItem2);

        PantryItemsAdapter adapter = new PantryItemsAdapter(getActivity(), items);

        itemListView.setAdapter(adapter);
    }
}
