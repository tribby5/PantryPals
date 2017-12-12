package pantrypals.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private String userId;
    private long oldestRecipeNegTimestamp;

    public ProfilePostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the recipe_layout first
        View view = inflater.inflate(R.layout.fragment_profile_posts, container, false);

        final FrameLayout fl = view.findViewById(R.id.profile_posts_layout);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Recipe> recipes = Lists.newArrayList();
                for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if(recipe.getPostedBy().keySet().iterator().next().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        recipe.setDbKey(recipeSnapshot.getKey());
                        recipes.add(recipe);
                    }
                }
                fl.addView(RecipeListView.newInstance(getContext(), recipes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
