package pantrypals.profile;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wefika.flowlayout.FlowLayout;

import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Group;
import pantrypals.models.Item;
import pantrypals.models.JointPantry;
import pantrypals.models.User;
import pantrypals.pantry.DatabaseRetriever;
import pantrypals.pantry.JointPantryAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    private static final String ARG_ID = "ID";
    private static final String TAG = "ProfileInfoFragment";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseRetriever retriever = new DatabaseRetriever();

    private HashMap<String, JointPantry> myJPantries;
    private JointPantryAdapter jointPantryAdapter;

    private User otherUser;

    public ProfileInfoFragment() {
        // Required empty public constructor
        myJPantries = new HashMap<>();
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
        getOtherUser();
        mDatabase.child("/userAccounts/" + getArguments().getCharSequence(ARG_ID) + "/preferences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlowLayout prefLayout = view.findViewById(R.id.prefs_container);
                prefLayout.removeAllViews();
                boolean addedPref = false;
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
                    addedPref = true;
                }
                if(!addedPref) {
                    TextView defaultText = new TextView(getContext());
                    defaultText.setText("No preferences.");
                    defaultText.setTextSize(14);
                    defaultText.setPadding(15, 15, 15, 15);
                    defaultText.setTextColor(getResources().getColor(R.color.colorHintDark));
                    prefLayout.addView(defaultText);
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
                boolean addedRestriction = false;
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
                    addedRestriction = true;
                }
                if(!addedRestriction) {
                    TextView defaultText = new TextView(getContext());
                    defaultText.setText("No restrictions.");
                    defaultText.setTextSize(14);
                    defaultText.setPadding(15, 15, 15, 15);
                    defaultText.setTextColor(getResources().getColor(R.color.colorHintDark));
                    restrictionLayout.addView(defaultText);
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
                boolean addedGroup = false;
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
                        addedGroup = true;
                    }
                }
                if(!addedGroup) {
                    TextView defaultText = new TextView(getContext());
                    defaultText.setText("No groups.");
                    defaultText.setTextSize(14);
                    defaultText.setPadding(15, 15, 15, 15);
                    defaultText.setTextColor(getResources().getColor(R.color.colorHintDark));
                    groupLayout.addView(defaultText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("/userAccounts/" + getArguments().getCharSequence(ARG_ID) + "/jointPantries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final FlowLayout jpLayout = view.findViewById(R.id.joint_pantries_container);
                jpLayout.removeAllViews();
                Map<String, Boolean> jps = (Map<String, Boolean>) dataSnapshot.getValue();
                if(jps != null) {
                    for (String jpID : jps.keySet()) {
                        mDatabase.child("pantries").child(jpID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                JointPantry jp = dataSnapshot.getValue(JointPantry.class);
                                if(jp != null) {
                                    TextView jpText = new TextView(getContext());
                                    jpText.setText(jp.getTitle());
                                    jpText.setTextSize(14);
                                    jpText.setPadding(25, 15, 25, 15);
                                    jpText.setTextColor(getResources().getColor(R.color.colorPurpleFade));
                                    jpText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_fade));

                                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(10, 10, 10, 10);

                                    jpText.setLayoutParams(params);
                                    jpLayout.addView(jpText);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    TextView defaultText = new TextView(getContext());
                    defaultText.setText("No joint pantries.");
                    defaultText.setTextSize(14);
                    defaultText.setPadding(15, 15, 15, 15);
                    defaultText.setTextColor(getResources().getColor(R.color.colorHintDark));
                    jpLayout.addView(defaultText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        renderSendInvBtn(view);

        return view;
    }

    private void getOtherUser() {
        String uid = getArguments().getCharSequence(ARG_ID).toString();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("userAccounts");
        userRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                otherUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void renderSendInvBtn(View view) {
        String otherId = getArguments().getCharSequence(ARG_ID).toString();
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        jointPantryAdapter = new JointPantryAdapter(getActivity(), myJPantries.values());
        myJPantries = retriever.retrieveJointPantries(myId, jointPantryAdapter);

        Button sendInvBtn = (Button) view.findViewById(R.id.sendInviteBtn);
        if (otherId.equals(myId)) {
            sendInvBtn.setVisibility(View.GONE);
            return;
        }

        sendInvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInviteModal(view);
            }
        });

    }

    private void openInviteModal(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.send_invite);

        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        View sendInviteModal = inflater.inflate(R.layout.modal_add_existing_object, null);
        TextView title = (TextView) sendInviteModal.findViewById(R.id.addExistingTitle);
        title.setText(R.string.invite_to_jpantry_title);

        renderJointPantryLV(sendInviteModal);

        builder.setView(sendInviteModal);
        builder.show();
    }

    private void renderJointPantryLV(View view) {
        ListView toAddItemsLV = (ListView) view.findViewById(R.id.existingObjectLV);
        toAddItemsLV.setAdapter(jointPantryAdapter);
        toAddItemsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JointPantry jPantry = jointPantryAdapter.getItem(i);
                openConfirmAddModal(view, jPantry);
            }
        });
    }

    private void openConfirmAddModal(View view, final JointPantry jPantry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.send_invite));

        LayoutInflater inflater = (LayoutInflater)getActivity().getLayoutInflater();

        View confirmInviteModal = inflater.inflate(R.layout.modal_confirm_invite, null);

        setTextValues(confirmInviteModal, jPantry);


        builder.setView(confirmInviteModal);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addUsertoOwners(jPantry);
            }
        });
        builder.show();
    }

    private void setTextValues(View view, JointPantry pantry) {
        TextView personTV = (TextView)view.findViewById(R.id.confirmInvPersonTV);
        personTV.setText(otherUser.getName());

        TextView pantryTV = (TextView)view.findViewById(R.id.confirmInvPantryTV);
        pantryTV.setText(pantry.getTitle());
    }

    private void addUsertoOwners(final JointPantry pantry) {
        final String uid = getArguments().getCharSequence(ARG_ID).toString();
        DatabaseReference pantryRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData));

        pantryRef.child(pantry.getDatabaseId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JointPantry jointPantry = dataSnapshot.getValue(JointPantry.class);
                Map<String,Boolean> owners = jointPantry.getOwnedBy();
                if(owners == null){
                    owners = new HashMap<>();
                }

                if (owners.containsKey(uid)) {
                    openInvalidAddModal();
                    return;
                }

                owners.put(uid, true);

                FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData))
                        .child(pantry.getDatabaseId()).child("ownedBy").setValue(owners)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                System.out.println("PRINT: successfully sent the invite");
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.users));
        userRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Map<String,Boolean> jPantries = user.getJointPantries();
                if (jPantries==null){
                    jPantries = new HashMap<>();
                }

                if (jPantries.containsKey(pantry.getDatabaseId())){
                    return;
                }

                jPantries.put(pantry.getDatabaseId(), true);

                FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.users))
                        .child(uid).child(getResources().getString(R.string.jointPantriesAttribute))
                        .setValue(jPantries);
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

        View invalidAddModal = inflater.inflate(R.layout.modal_invalid_add_item, null);
        TextView message = invalidAddModal.findViewById(R.id.invalidAddTV);
        message.setText(R.string.invalidInviteText);

        invalidBuilder.setView(invalidAddModal);
        invalidBuilder.show();
    }

}
