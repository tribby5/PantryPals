package pantrypals.pantry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pantrypals.models.JointPantry;
import pantrypals.models.User;


public class PantryTabFragment extends Fragment {
    private ArrayList<HashMap<String, String>> joint;
    private JointPantryAdapter jointAdapter;
    private ListView jointPantries;
    private DatabaseReference ref;

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

        setupJointPantryLV(view);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupJointPantryLV(View view){
        //JOINT PANTRY
        joint = new ArrayList<HashMap<String, String>>();
        jointPantries = (ListView) view.findViewById(R.id.jointPantriesListView);
        jointAdapter = new JointPantryAdapter(getActivity(), joint);
        jointPantries.setAdapter(jointAdapter);

        jointPantries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switchToJointPantry(view);
            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts));
        ref = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Map<String, Boolean> jointID = user.getJointPantries();

                for(String pantryID : jointID.keySet()){
                    DatabaseReference jointRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.jointPantriesData)).child(pantryID);
                    jointRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            HashMap<String, String> values = new HashMap<>();

                            String key = dataSnapshot.getKey();
                            JointPantry pantry = dataSnapshot.getValue(JointPantry.class);
                            String title = pantry.getTitle();

                            values.put(JointPantryAdapter.KEY, key);
                            values.put(JointPantryAdapter.TITLE, title);

                            joint.add(values);
                            jointAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addJointButtonClick(View v){

    }

    private void switchToPersonalPantry(View view){
        PersonalPantryFragment personalPantry = new PersonalPantryFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(getId(), this).addToBackStack(null); // so that back button works
        transaction.replace(R.id.frame_layout, personalPantry);
        transaction.commit();
    }

    private void switchToJointPantry(View view){
        JointPantryFragment jointPantry = new JointPantryFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(getId(), this).addToBackStack(null); // so that back button works
        transaction.replace(R.id.frame_layout, jointPantry);
        transaction.commit();
    }


}
