package pantrypals.pantry;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.databaes.pantrypals.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;
import pantrypals.models.Pantry;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalPantryFragment extends Fragment {

    private static final String TAG = "PersonalPantryFragment";


    private ArrayList<Item> items;
    private PantryItemsAdapter adapter;
    private String add_item_name;
    private double add_item_amount;
    private String add_item_units;
    private String add_item_date;
    private DatabaseReference databaseRef;
    private String pantryID;
    private Pantry myPantry;
    private DatabaseReference pantryRef;
    private DatabaseReference itemsRef;
    private PantryItemsAdapter itemsAdapter;
    private ItemsRetriever itemsRetriever;


    public PersonalPantryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_pantry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButtonClick(view);
            }
        });
        databaseRef = FirebaseDatabase.getInstance().getReference();
        pantryID = this.getArguments().getString("pantryID");
        items = new ArrayList<>();
        itemsAdapter = new PantryItemsAdapter(getActivity(), items);
        getMyPantry();
        itemsRetriever = new ItemsRetriever(pantryID, itemsAdapter);
        items = itemsRetriever.retrievePantryItems();
        Log.d(TAG, "NUM OF ITEMS FROM THE RETRIEVER: " + items.size());
        setupItemListView(view);

        super.onViewCreated(view, savedInstanceState);
    }

    private void getMyPantry() {
        pantryRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData));

        // TODO: if the user doesn't have a pantry, we need to make one for them!
        pantryRef.child(pantryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myPantry = dataSnapshot.getValue(Pantry.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void setupItemListView(View view){
        ListView itemListView = (ListView) view.findViewById(R.id.personalPantryItemsLV);
        itemListView.setAdapter(itemsAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editModal(view);
            }
        });



    }

//    private void retrievePantryItems() {
//        itemsRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.items));
//        pantryRef.child(pantryID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Pantry pantry = dataSnapshot.getValue(JointPantry.class);
//                for (String itemID : pantry.getItems().keySet()){
//                    System.out.println("PRINT: itemID = "+itemID);
//                    itemsRef.child(itemID).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Item item = dataSnapshot.getValue(Item.class);
//                            System.out.println("PRINT: item name = "+item.getName());
//                            items.add(item);
//                            System.out.println("PRINT: items.size() = "+items.size());
//                            itemsAdapter.refresh(items);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                itemsAdapter.refresh(items);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void addButtonClick(final View v){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.modal_add_item));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.modal_add_item, null);

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
                add_item_date = itemDate.getText().toString();

                Map<String,Boolean> myItems = myPantry.getItems();

                DatabaseReference itemsRef = databaseRef.child(getResources().getString(R.string.items));
                String itemID = itemsRef.push().getKey();
                Item newItem = new Item();
                newItem.setName(add_item_name);
                newItem.setAmount(add_item_amount);
                newItem.setUnit(add_item_units);
                newItem.setExpiration(add_item_date);
                itemsRef.child(itemID).setValue(newItem);

                myItems.put(itemID, true);

                FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData))
                        .child(pantryID).child(getResources().getString(R.string.items)).setValue(myItems);


                items = itemsRetriever.retrievePantryItems();
                itemsAdapter.refresh(items);

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

    private void editModal(View v){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.modal_edit_item));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.modal_edit_item, null);

        builder.setView(dialogView);

        builder.show();
    }



}
