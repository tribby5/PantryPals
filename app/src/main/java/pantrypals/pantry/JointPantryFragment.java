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
import android.widget.ListView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;

/**
 * A simple {@link Fragment} subclass.
 */
public class JointPantryFragment extends Fragment {

    private String masterTable = "pantry-pals";
    private String itemsTable = "jointPantryItems";
    private String pantriesTable = "pantries";
    private String jointPantryID;
    private String personalPantryID;

    private DatabaseReference pantryRef;
    private PantryItemsAdapter jointItemsAdapter;
    private HashMap<String,Item> jointPantryItems;
    private HashMap<String, Item> personalPantryItems;
    private PantryItemsAdapter toAddItemsAdapter;
    private AlertDialog.Builder builder;
    private View addItemModal;
    private View confirmAddModal;
    private View invalidAddModal;
    private DatabaseRetriever retriever;
    private JointPantry jointPantry;

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
        builder = new AlertDialog.Builder(getActivity());
        retriever = new DatabaseRetriever();

        jointPantryID = this.getArguments().getString("jointPantryID");
        personalPantryID = this.getArguments().getString("personalPantryID");

        jointPantryItems = new HashMap<>();
        jointItemsAdapter = new PantryItemsAdapter(getActivity(), jointPantryItems.values());
        jointPantryItems = retriever.retrievePantryItems(jointPantryID, jointItemsAdapter);

        setupItemListView(view);

        FloatingActionButton addItemsBtn = (FloatingActionButton) view.findViewById(R.id.add_item_my_pantry_btn);
        addItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddItemModal(view);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupItemListView(View view){
        ListView itemListView = (ListView) view.findViewById(R.id.jointPantryItemsLV);
        itemListView.setAdapter(jointItemsAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //editModal(view);
            }
        });

    }

    private void setupToAddItemListView(View view) {
        ListView toAddItemsLV = (ListView) view.findViewById(R.id.existingObjectLV);
        toAddItemsLV.setAdapter(toAddItemsAdapter);
        toAddItemsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = toAddItemsAdapter.getItem(i);
                System.out.println("PRINT: click on item "+item.getName()+" "+item.getDatabaseId());
                openConfirmAddModal(view, item);
            }
        });
    }

    private void openAddItemModal(View view) {
        builder.setTitle(getResources().getString(R.string.modal_add_item));

        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        addItemModal = inflater.inflate(R.layout.modal_add_existing_object, null);
        TextView title = (TextView) addItemModal.findViewById(R.id.addExistingTitle);
        title.setText(R.string.add_item_from_my_pantry_title);

        personalPantryItems = new HashMap<>();
        toAddItemsAdapter = new PantryItemsAdapter(getActivity(), personalPantryItems.values());
        personalPantryItems = retriever.retrievePantryItems(personalPantryID, toAddItemsAdapter);

        setupToAddItemListView(addItemModal);

        builder.setView(addItemModal);
        builder.show();
    }

    private void openConfirmAddModal(View view, final Item item) {
        builder.setTitle(getResources().getString(R.string.modal_add_item));

        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        confirmAddModal = inflater.inflate(R.layout.modal_confirm_add, null);

        TextView itemToAdd = (TextView) confirmAddModal.findViewById(R.id.confirmAddItemTV);
        itemToAdd.setText(item.getName());

        builder.setView(confirmAddModal);
        builder.setPositiveButton(R.string.add_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("PRINT: adding "+item.getName()+" to joint pantry "+jointPantryID);
                getJointPantry(item);
            }
        });
        builder.show();
    }

    private void getJointPantry(final Item item) {
        pantryRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData));

        pantryRef.child(jointPantryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                jointPantry = dataSnapshot.getValue(JointPantry.class);
                System.out.println("PRINT: got joint Pantry = "+jointPantry.getTitle());
                Map<String,Boolean> myItems = jointPantry.getItems();
                if(myItems == null){
                    myItems = new HashMap<>();
                }

                if (myItems.containsKey(item.getDatabaseId())) {
                    openInvalidAddModal();
                    return;
                }

                myItems.put(item.getDatabaseId(), true);

                FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData))
                        .child(jointPantryID).child(getResources().getString(R.string.items)).setValue(myItems)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                jointPantryItems = retriever.retrievePantryItems(jointPantryID, jointItemsAdapter);
                                jointItemsAdapter.refresh(jointPantryItems.values());
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openInvalidAddModal() {
        AlertDialog.Builder invalidBuilder = new AlertDialog.Builder(getActivity());
        invalidBuilder.setTitle(getResources().getString(R.string.modal_add_item));

        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        invalidAddModal = inflater.inflate(R.layout.modal_invalid_add_item, null);
        TextView message = invalidAddModal.findViewById(R.id.invalidAddTV);
        message.setText(R.string.invalid_add_text);

        invalidBuilder.setView(invalidAddModal);
        invalidBuilder.show();
    }


}
