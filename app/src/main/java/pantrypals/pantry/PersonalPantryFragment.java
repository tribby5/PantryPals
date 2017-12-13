package pantrypals.pantry;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.Pantry;
import pantrypals.models.User;

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
    private User user;
    private View view;


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
        this.view = view;
        getMyPantry();

        super.onViewCreated(view, savedInstanceState);
    }

    private void getMyPantry() {

        pantryRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData));

        if(pantryID == null){
            createPersonalPantry();
        }

        pantryRef.child(pantryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myPantry = dataSnapshot.getValue(Pantry.class);
                fillPantry();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //TODO method won't be needed after the old auto-generated users are gone
    private void createPersonalPantry(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Create entry for a personal pantry:
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData));
        Pantry newPantry = new Pantry();
        HashMap<String, Boolean> ownedBy = new HashMap<>();
        ownedBy.put(uid, true);
        newPantry.setOwnedBy(ownedBy);
        newPantry.setShared(false);
        final String key = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.pantriesData)).push().getKey();
        ref.child(key).setValue(newPantry);

        //Set Pantry for user
        DatabaseReference accounts = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts));
        accounts.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                user.setPersonalPantry(key);
                updateUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set instance variable
        pantryID = key;
    }


    private void updateUser(){
        FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
    }

    //Assumes pantry exists
    private void fillPantry(){
        itemsRetriever = new ItemsRetriever(pantryID, itemsAdapter);
        items = itemsRetriever.retrievePantryItems();
        setupItemListView(view);
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
                add_item_amount = (itemAmount.getText() != null) ? Double.parseDouble(itemAmount.getText().toString()) : 0.0;
                add_item_date = itemDate.getText().toString();


                Map<String,Boolean> myItems = myPantry.getItems();
                if(myItems == null){
                    myItems = new HashMap<>();
                }

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
                        .child(pantryID).child(getResources().getString(R.string.items)).setValue(myItems).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        items = itemsRetriever.retrievePantryItems();
                        itemsAdapter.refresh(items);
                    }
                });

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
