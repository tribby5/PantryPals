package pantrypals.pantry;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pantrypals.models.GroceryItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryListFragment extends Fragment {
    private List<Map<String, String>> items;
    private ListView grocery;
    private GroceryItemAdapter adapter;

    private String add_item_name;
    private Double add_item_amount;
    private String add_item_units;

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
        grocery.setAdapter(adapter);

        //Load in data from Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.groceryData)).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    GroceryItem item = child.getValue(GroceryItem.class);
                    Map<String, String> data = new HashMap<String, String>();
                    data.put(GroceryItemAdapter.GROCERY_TITLE, item.getName());
                    data.put(GroceryItemAdapter.GROCERY_AMOUNT, Double.toString(item.getAmount()));
                    data.put(GroceryItemAdapter.GROCERY_UNIT, item.getUnit());
                    data.put(GroceryItemAdapter.GROCERY_ITEM_ID, child.getKey());

                    items.add(data);
                }
                adapter.refresh(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setupAddGrocery(View view){
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemModal(view);
            }
        });
    }

    private void addItemModal(View view){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.modal_add_item));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.modal_add_item_grocery, null);

        setMeasurementSpinner(dialogView);
        builder.setView(dialogView);
        builder.setPositiveButton(getResources().getText(R.string.add_item), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText itemName = (EditText) dialogView.findViewById(R.id.add_item_name);
                EditText itemAmount = (EditText) dialogView.findViewById(R.id.add_item_amount);
                EditText itemDate = (EditText) dialogView.findViewById(R.id.add_item_date);
                add_item_name = itemName.getText().toString();
                add_item_amount = Double.parseDouble(itemAmount.getText().toString());

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference groceryRef = FirebaseDatabase.getInstance().getReference()
                        .child(getResources().getString(R.string.groceryData)).child(uid);
                String itemID = groceryRef.push().getKey();
                GroceryItem newGrocery = new GroceryItem();
                newGrocery.setName(add_item_name);
                newGrocery.setAmount(add_item_amount);
                newGrocery.setUnit(add_item_units);

                groceryRef.child(itemID).setValue(newGrocery);

                Map<String, String> newItem = new HashMap<>();
                newItem.put(GroceryItemAdapter.GROCERY_TITLE, add_item_name);
                newItem.put(GroceryItemAdapter.GROCERY_AMOUNT, Double.toString(add_item_amount));
                newItem.put(GroceryItemAdapter.GROCERY_UNIT, add_item_units);
                newItem.put(GroceryItemAdapter.GROCERY_ITEM_ID, itemID);

                items.add(newItem);
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void setMeasurementSpinner(View v){
        final Spinner unitSpinner = v.findViewById(R.id.add_item_units);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(v.getContext(),
                R.array.units_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    add_item_units = unitSpinner.getItemAtPosition(position).toString();
                    unitSpinner.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }





}
