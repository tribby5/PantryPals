package pantrypals.pantry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pantrypals.models.Item;
import pantrypals.models.JointPantry;
import pantrypals.models.User;


public class PantryTabFragment extends Fragment {
    private JointPantryAdapter jointAdapter;
    private ListView jointPantriesLV;

    private Map<String, JointPantry> jointPantries;
    private DatabaseReference databaseRef;
    private String masterTable = "pantry-pals";
    private String pantriesTable = "pantries";
    private ChildEventListener pantryListener;
    private ChildEventListener usersListener;

    private DatabaseRetriever retriever;

    private String myUserID;
    private String personalPantryID;

    Map<String, Item> items;

    public PantryTabFragment() {
        // Required empty public constructor
    }

    public static PantryTabFragment newInstance() {
        PantryTabFragment fragment = new PantryTabFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jointPantries = new HashMap<>();
        retriever = new DatabaseRetriever();
        initDatabase();
        myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getPersonalPantryID();
        jointAdapter = new JointPantryAdapter(getActivity(), jointPantries.values());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the recipe_layout for this fragment
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button addJoint = (Button) view.findViewById(R.id.addJointPantry);
        addJoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJointButtonClick(view);
            }
        });

        Button myPantry = (Button) view.findViewById(R.id.my_pantry);
        myPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switchToPersonalPantry(view);
            }
        });


        FloatingActionButton grocery = (FloatingActionButton) view.findViewById(R.id.fab);
        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToGroceryList(view);
            }
        });

        jointAdapter = new JointPantryAdapter(getActivity(), jointPantries.values());

        jointPantries = retriever.retrieveJointPantries(myUserID, jointAdapter);


        setupJointPantryLV(view);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseRef.child(pantriesTable).removeEventListener(pantryListener);
    }

    public boolean initDatabase () {

        pantryListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                JointPantry pantry = dataSnapshot.getValue(JointPantry.class);
                jointPantries.put(pantry.getDatabaseId(), pantry);
                jointAdapter.refresh(jointPantries.values());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                onChildAdded(dataSnapshot, prevChildKey);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                JointPantry pantry = dataSnapshot.getValue(JointPantry.class);
                jointPantries.remove(pantry.getDatabaseId());
                jointAdapter.refresh(jointPantries.values());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        usersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println("PRINT: "+dataSnapshot);
                System.out.println("PRINT: personal pantry ID = "+user.getPersonalPantry());
                personalPantryID = user.getPersonalPantry();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        databaseRef = FirebaseDatabase.getInstance().getReference().child(masterTable);
        databaseRef.child(getResources().getString(R.string.pantries)).addChildEventListener(pantryListener);


        return true;
    }

    private void setupJointPantryLV(View view){
        //JOINT PANTRY
        jointPantriesLV = (ListView) view.findViewById(R.id.jointPantriesListView);
        jointPantriesLV.setAdapter(jointAdapter);

        jointPantriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                System.out.println("PRINT: joint pantries listview position = "+position);
                switchToJointPantry(view, position);
            }
        });

//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts));
//        ref = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//               // joint = new ArrayList<>();
//                User user = dataSnapshot.getValue(User.class);
//                Map<String, Boolean> jointID = user.getJointPantries();
////                System.out.println("PRINT: jointID.keySet.size = "+jointID.size());
//                if(jointID != null) {
//
//                    for (final String pantryID : jointID.keySet()) {
////                        System.out.println("PRINT: pantryID = "+pantryID);
//                        final DatabaseReference jointRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData)).child(pantryID);
//
//                        jointRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                JointPantry pantry = dataSnapshot.getValue(JointPantry.class);
//
//                                if (pantry != null) {
//                                    pantry.setDatabaseId(pantryID);
//                                    jointPantries.put(pantryID, pantry);
//                                    jointAdapter.refresh(jointPantries.values());
//
//                                }
//
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    private void addJointButtonClick(View v){
        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.add_new_joint_pantry));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.modal_add_joint_pantry, null);

        builder.setView(dialogView);
        final EditText titleInput = dialogView.findViewById(R.id.titleInput);
        final EditText shareInput = dialogView.findViewById(R.id.shareInput);


        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //https://stackoverflow.com/questions/41953388/java-split-and-trim-in-one-shot
                List<String> emails = Arrays.asList(shareInput.getText().toString().trim().split("\\s*,\\s*"));

                createNewJointPantry(titleInput.getText().toString(), emails);
            }
        });

        builder.show();

    }

    private void getPersonalPantryID() {
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts));
        System.out.println("PRINT: my user ID = "+myUserID);
        parentRef.child(myUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println("PRINT: personal pantry ID = " + user.getPersonalPantry());
                personalPantryID = user.getPersonalPantry();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void switchToPersonalPantry(View view){
        PersonalPantryFragment personalPantry = new PersonalPantryFragment();
        Bundle args = new Bundle();
        args.putString("pantryID", personalPantryID);
        System.out.println("PRINT: putting personal pantry id = "+personalPantryID);
        personalPantry.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(getId(), this).addToBackStack(null); // so that back button works
        transaction.replace(R.id.frame_layout, personalPantry);
        transaction.commit();
    }

    private void switchToJointPantry(View view, int position){
        JointPantryFragment jointFrag = new JointPantryFragment();
        String pantryID = jointAdapter.getPantryIdFromDataSource(position);
        Bundle args = new Bundle();
        args.putString("jointPantryID", pantryID);
        args.putString("personalPantryID", personalPantryID);
        System.out.println("PRINT: storing joint pantryID = "+pantryID);
        System.out.println("PRINT: storing persona; pantryID = "+personalPantryID);
        jointFrag.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(getId(), this).addToBackStack(null); // so that back button works
        transaction.replace(R.id.frame_layout, jointFrag);
        transaction.commit();
    }

    private void switchToGroceryList(View view){
        GroceryListFragment groceryListFragment = new GroceryListFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(getId(), this).addToBackStack(null); // so that back button works
        transaction.replace(R.id.frame_layout, groceryListFragment);
        transaction.commit();
    }

    private void createNewJointPantry(String title, List<String> emails){
        DatabaseReference homeRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = homeRef.child(getResources().getString(R.string.userAccounts));
        final JointPantry pantry = new JointPantry();
        pantry.setTitle(title);
        pantry.setShared(true);

        final ArrayList<String> uids = new ArrayList<>();
        for(String email : emails){

            Query result = usersRef.orderByChild(getResources().getString(R.string.email)).equalTo(email);
            if(result != null){
                result.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            uids.add(snapshot.getKey());
                        }
                        storeNewPantryData(pantry, uids);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }

    }

    private void storeNewPantryData(JointPantry pantry, ArrayList<String> uids){
        HashMap<String, Boolean> sharedWith = new HashMap<>();
        for(String uid : uids){
            sharedWith.put(uid, true);
        }
        sharedWith.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
        pantry.setOwnedBy(sharedWith);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference(getResources().getString(R.string.pantriesData)).push().getKey();
        database.getReference().child(getResources().getString(R.string.pantriesData)).child(key).setValue(pantry);


        //Reflect Change in UI
        jointPantries.put(pantry.getDatabaseId(), pantry);
        jointAdapter.refresh(jointPantries.values());

        //Update UserAccounts jointPantries references
        storeJointPantryRefInUsers(uids, key);
    }

    private void storeJointPantryRefInUsers(ArrayList<String> uids, String pantryKey){
        final String key = pantryKey;
        uids.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts));
        for(final String uid : uids){
            Log.d("WORKS", uid);
            parentRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("PRINT: working datasnapshot = "+dataSnapshot);
                    User user = dataSnapshot.getValue(User.class);
                    Map<String, Boolean> jointPantries = user.getJointPantries();
                    if(jointPantries == null){
                        jointPantries = new HashMap<String, Boolean>();
                    }
                    jointPantries.put(key, true);
                    user.setJointPantries(jointPantries);
                    parentRef.child(uid).setValue(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }




}
