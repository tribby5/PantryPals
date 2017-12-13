package pantrypals.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import pantrypals.activities.NewRecipeActivity;
import pantrypals.database.generate.RecipeGenerator;
import pantrypals.models.Post;
import pantrypals.models.Recipe;
import pantrypals.models.TempRecipe;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String userId;

    private ListView feedListView;
    private ArrayList<Recipe> feedList = new ArrayList<>();
    private CustomListAdapter adapter;
    private long oldestRecipeNegTimestamp;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: Use the following current user information for more intelligent feed later on
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        ref = mFirebaseDatabase.getReference("/recipes");

        ref.orderByChild("negTimestamp").limitToFirst(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
                        oldestRecipeNegTimestamp = recipe.getNegTimestamp();
                        String recipeId = snapshot.getKey();
                        recipe.setDbKey(recipeId);
                        // filter based on whether i follow this person or not
                        final String currUserId = userId;
                        Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
                        final String postedBy = recipePostedBySet.iterator().next();
                        DatabaseReference mFollow = FirebaseDatabase.getInstance().getReference("/follows");
                        if (currUserId.equals(postedBy)) {
                            adapter.add(recipe);
                        } else {
                            mFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(currUserId).hasChild(postedBy)) {
                                        adapter.add(recipe);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        Log.d(TAG, "Retrieved Id: " + recipeId);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Inflate the recipe_layout first
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // newPostButton
        Button newPostButton = (Button) view.findViewById(R.id.newPostButton);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postIntent = new Intent(getActivity(), NewRecipeActivity.class);
                startActivity(postIntent);
            }
        });

        feedListView = (ListView) view.findViewById(R.id.feedListView);
        adapter = new CustomListAdapter(getActivity(), R.layout.card_layout_main, feedList);
        feedListView.setAdapter(adapter);

        // Implement scrolling
        feedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                currentScrollState = scrollState;
                isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
                totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && currentScrollState == SCROLL_STATE_IDLE) {
                    ref.orderByChild("negTimestamp").startAt(oldestRecipeNegTimestamp).limitToFirst(5)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final Recipe recipe = snapshot.getValue(Recipe.class);
                                        if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
                                            oldestRecipeNegTimestamp = recipe.getNegTimestamp();
                                            String recipeId = snapshot.getKey();
                                            recipe.setDbKey(recipeId);
                                            // filter based on whether i follow this person or not
                                            final String currUserId = userId;
                                            Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
                                            final String postedBy = recipePostedBySet.iterator().next();
                                            DatabaseReference mFollow = FirebaseDatabase.getInstance().getReference("/follows");

                                            // If recipe is written by me (my recipe) then show
                                            if (currUserId.equals(postedBy)) {
                                                adapter.add(recipe);
                                            } else {
                                                // this recipe is not written by me - check if i follow this person
                                                mFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.child(currUserId).hasChild(postedBy)) {
                                                            adapter.add(recipe);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                            Log.d(TAG, "Retrieved Id: " + recipeId);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
        return view;
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
