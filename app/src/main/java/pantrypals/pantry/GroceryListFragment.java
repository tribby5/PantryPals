package pantrypals.pantry;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryListFragment extends Fragment {
    private List<Map<String, String>> items;
    private ListView grocery;
    private GroceryItemAdapter adapter;

    public GroceryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_grocery_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupListView(view);
        setupAddGrocery(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupListView(View view){
        grocery = view.findViewById(R.id.grocery_lv);
        items = new ArrayList<>();
        adapter = new GroceryItemAdapter(getActivity(), items);
    }

    private void setupAddGrocery(View view){
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemModal();
            }
        });
    }

    private void addItemModal(){
        //wait for Ali's code for this
    }





}
