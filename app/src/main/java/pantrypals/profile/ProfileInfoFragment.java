package pantrypals.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wefika.flowlayout.FlowLayout;

import pantrypals.models.Group;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    private static final String ARG_ID = "ID";
    private static final String TAG = "ProfileInfoFragment";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    public static ProfileInfoFragment newFragment(CharSequence uid) {
        ProfileInfoFragment fragment = new ProfileInfoFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_ID, uid);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        mDatabase.child("/userAccounts/" + getArguments().getCharSequence(ARG_ID) + "/preferences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlowLayout prefLayout = view.findViewById(R.id.prefs_container);
                prefLayout.removeAllViews();
                for(DataSnapshot pref : dataSnapshot.getChildren()) {
                    TextView prefText = new TextView(getContext());
                    prefText.setText(pref.getKey());
                    prefText.setTextSize(14);
                    prefText.setPadding(25, 15, 25, 15);
                    prefText.setTextColor(getResources().getColor(R.color.colorPurpleFade));
                    prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_fade));

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,10,10,10);

                    prefText.setLayoutParams(params);
                    prefLayout.addView(prefText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("/userAccounts/" + getArguments().getCharSequence(ARG_ID) + "/restrictions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlowLayout restrictionLayout = view.findViewById(R.id.restrictions_container);
                restrictionLayout.removeAllViews();
                for(DataSnapshot restriction : dataSnapshot.getChildren()) {
                    TextView restrictionText = new TextView(getContext());
                    restrictionText.setText(restriction.getKey());
                    restrictionText.setTextSize(14);
                    restrictionText.setPadding(25, 15, 25, 15);
                    restrictionText.setTextColor(getResources().getColor(R.color.colorPurpleFade));
                    restrictionText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_fade));

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,10,10,10);

                    restrictionText.setLayoutParams(params);
                    restrictionLayout.addView(restrictionText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("/groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlowLayout groupLayout = view.findViewById(R.id.groups_container);
                groupLayout.removeAllViews();
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if(group.getMembers().keySet().contains(getArguments().getCharSequence(ARG_ID))) {
                        TextView groupText = new TextView(getContext());
                        groupText.setText(group.getName());
                        groupText.setTextSize(14);
                        groupText.setPadding(25, 15, 25, 15);
                        groupText.setTextColor(getResources().getColor(R.color.colorPurpleFade));
                        groupText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_fade));

                        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10,10,10,10);

                        groupText.setLayoutParams(params);
                        groupLayout.addView(groupText);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
