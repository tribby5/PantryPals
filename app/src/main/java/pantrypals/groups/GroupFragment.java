package pantrypals.groups;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import pantrypals.activities.NewRecipeActivity;
import pantrypals.home.CustomListAdapter;
import pantrypals.models.Group;
import pantrypals.models.Recipe;
import pantrypals.profile.ProfileFragment;
import pantrypals.profile.ProfileInfoFragment;
import pantrypals.profile.ProfilePostsFragment;
import pantrypals.profile.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private static final String TAG = "GroupFragment";

    private static final String ARG_ID = "ID";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private String id;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newFragment(String gid) {
        GroupFragment fragment = new GroupFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ID, gid);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            id = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_group, container, false);

        final ListView groupFeed = view.findViewById(R.id.group_feed);

        mDatabase.child("/groups/" + id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Group group = dataSnapshot.getValue(Group.class);
                final Map<String, Boolean> members = group.getMembers();
                final String groupID = dataSnapshot.getKey();

                TextView nameTV = view.findViewById(R.id.group_name);
                TextView membersTV = view.findViewById(R.id.group_members);
                Button joinBtn = view.findViewById(R.id.group_join_btn);
                Button addRecipeBtn = view.findViewById(R.id.group_new_post_btn);

                final TextView notInGroupTV = view.findViewById(R.id.group_not_member_message);

                nameTV.setText(group.getName());
                membersTV.setText(members.size() + " members");

                final String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final boolean inGroup = members.containsKey(myID);

                if(inGroup) {
                    joinBtn.setText("Leave Group");
                    joinBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    notInGroupTV.setVisibility(View.INVISIBLE);
                    groupFeed.setVisibility(View.VISIBLE);
                } else {
                    joinBtn.setText("Join Group");
                    joinBtn.setBackgroundColor(getResources().getColor(R.color.colorGrayText));
                    groupFeed.setVisibility(View.INVISIBLE);
                    notInGroupTV.setVisibility(View.VISIBLE);
                }

                joinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(inGroup) {
                            members.remove(myID);
                            groupFeed.setVisibility(View.INVISIBLE);
                            notInGroupTV.setVisibility(View.VISIBLE);
                        } else {
                            members.put(myID, true);
                            notInGroupTV.setVisibility(View.INVISIBLE);
                            groupFeed.setVisibility(View.VISIBLE);
                        }
                        group.setMembers(members);
                        mDatabase.child("/groups").child(id).setValue(group);
                    }
                });

                addRecipeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent postIntent = new Intent(getActivity(), NewRecipeActivity.class);
                        postIntent.putExtra(NewRecipeActivity.CONTEXT_KEY, dataSnapshot.getKey());
                        startActivity(postIntent);
                    }
                });

                mDatabase.child("recipes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Recipe> recipes = Lists.newArrayList();
                        for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                            if(recipe.getGroupId() != null && recipe.getGroupId().equals(groupID)) {
                                recipe.setDbKey(recipeSnapshot.getKey());
                                recipes.add(recipe);
                            }
                        }
                        groupFeed.setAdapter(new CustomListAdapter(getContext(), R.layout.card_layout_main, recipes, getActivity()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
