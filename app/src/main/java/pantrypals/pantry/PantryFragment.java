package pantrypals.pantry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;

import java.util.ArrayList;


public class PantryFragment extends Fragment {

    public PantryFragment() {
        // Required empty public constructor
    }

    public static PantryFragment newInstance() {
        PantryFragment fragment = new PantryFragment();
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

    private void setupJointPantryLV(View view){
        //JOINT PANTRY
        ListView jointPantries = (ListView) view.findViewById(R.id.jointPantriesListView);

        jointPantries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switchToJointPantry(view);
            }
        });

        //TODO remove hard code:
        //help for hardcoding from: http://windrealm.org/tutorials/android/android-listview.php
        ArrayList<String> joint = new ArrayList<String>();
        joint.add("Fourth floor cooks");
        joint.add("Apt 421");

        ArrayAdapter<String> jointAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, joint);
        jointPantries.setAdapter(jointAdapter);
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
