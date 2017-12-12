package pantrypals.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pantrypals.activities.NewRecipeActivity;
import pantrypals.home.CustomListAdapter;
import pantrypals.models.Recipe;
import pantrypals.util.AuthUserInfo;

import pantrypals.activities.NewRecipeActivity;
import pantrypals.home.CustomListAdapter;
import pantrypals.models.Recipe;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilePostsFragment extends Fragment {

    private static final String ARG_ID = "ID";
    private static final String TAG = "ProfilePostsFragment";

    public ProfilePostsFragment() {
        // Required empty public constructor
    }

    public static ProfilePostsFragment newFragment(CharSequence uid) {
        ProfilePostsFragment fragment = new ProfilePostsFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_ID, uid);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the recipe_layout first
        View view = inflater.inflate(R.layout.fragment_profile_posts, container, false);

        final LinearLayout ll = view.findViewById(R.id.profile_posts_linear_layout);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Recipe> recipes = Lists.newArrayList();
                for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if(recipe.getPostedBy().keySet().iterator().next().equals(getArguments().get(ARG_ID))) {
                        recipe.setDbKey(recipeSnapshot.getKey());
                        recipes.add(recipe);
                    }
                }
                for(int i = 0; i < recipes.size(); i++) {
                    CustomListAdapter adapter = new CustomListAdapter(getContext(), R.layout.card_layout_main, recipes);
                    ll.addView(adapter.getView(i, null, null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
