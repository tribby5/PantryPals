package pantrypals.home;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.analytics.FirebaseAnalytics;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import pantrypals.activities.NewRecipeActivity;
import pantrypals.database.generate.RecipeGenerator;
import pantrypals.models.Item;
import pantrypals.models.Pantry;
import pantrypals.models.Post;
import pantrypals.models.Recipe;
import pantrypals.models.TempRecipe;
import pantrypals.models.User;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String userId;

    private ListView feedListView;
    private ArrayList<Recipe> feedList = new ArrayList<>();
    private CustomListAdapter adapter;
    private long oldestRecipeNegTimestamp;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        ref = mFirebaseDatabase.getReference("/recipes");
        ref.orderByChild("negTimestamp").limitToFirst(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
                        oldestRecipeNegTimestamp = recipe.getNegTimestamp();
                        final String recipeId = snapshot.getKey();
                        recipe.setDbKey(recipeId);
                        // get the group this recipe belongs to
                        final String groupId = recipe.getGroupId();

                        // filter based on whether i follow this person or not
                        final String currUserId = userId;
                        Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
                        final String postedBy = recipePostedBySet.iterator().next();

                        DatabaseReference mFollow = mFirebaseDatabase.getReference();
                        if (currUserId.equals(postedBy)) {
                            feedList.add(recipe);
                            adapter.notifyDataSetChanged();
                        } else {
                            mFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean display = false;
                                    boolean relevant = false;
                                    // Check if I follow this author and if my setting is only following relevant recipes
                                    if (dataSnapshot.child("/follows").hasChild(userId)) {
                                        display = dataSnapshot.child("/follows").child(userId).hasChild(postedBy);
                                        if (display) {
                                            relevant = dataSnapshot.child("/follows").child(userId).child(postedBy).getValue(String.class).equals("relevant");
                                        }
                                    }

                                    // Ingredient matching if I only follow relevant
                                    if (relevant) {
                                        // First find my pantry
                                        Pantry pantry = null;
                                        for (DataSnapshot ds : dataSnapshot.child("/pantries").getChildren()) {
                                            Pantry p = ds.getValue(Pantry.class);
                                            if (p.getOwnedBy().containsKey(userId)) {
                                                // this is user's pantry
                                                pantry = p;
                                                break;
                                            }
                                        }
                                        // Get all items in pantry into a list
                                        if (pantry == null || pantry.getItems() == null || pantry.getItems().size() == 0) {
                                            // No items, do not display
                                            display = false;
                                        } else {
                                            // Just get item strings for now
                                            List<String> itemNames = new ArrayList<>();
                                            Set<String> itemIds = pantry.getItems().keySet();
                                            for (String itemId : itemIds) {
                                                if (dataSnapshot.child("/items").hasChild(itemId)) {
                                                    Item i = dataSnapshot.child("/items").child(itemId).getValue(Item.class);
                                                    itemNames.add(i.getName());
                                                }
                                            }
                                            // Now with a list of item names, compare with recipe's item names
                                            if (!hasIngredients(recipe, itemNames)) {
                                                display = false;
                                            }
                                        }
                                    }

                                    // Check if I'm in this group - if I am, this overrides follow/relevant settings for group recipes
                                    if (groupId != null) {
                                        if (dataSnapshot.child("/group").hasChild(userId)) {
                                            display = dataSnapshot.child("/group").child(userId).hasChild(groupId);
                                        }
                                    }

                                    if (display) {
                                        feedList.add(recipe);
                                        adapter.notifyDataSetChanged();
                                        log("Display_recipe", recipeId);
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
        adapter = new CustomListAdapter(getActivity(), R.layout.card_layout_main, feedList, getActivity());
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
                    ref.orderByChild("negTimestamp").startAt(oldestRecipeNegTimestamp).limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                final Recipe recipe = snapshot.getValue(Recipe.class);
                                if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
                                    oldestRecipeNegTimestamp = recipe.getNegTimestamp();
                                    String recipeId = snapshot.getKey();
                                    recipe.setDbKey(recipeId);
                                    // get the group this recipe belongs to
                                    final String groupId = recipe.getGroupId();

                                    // filter based on whether i follow this person or not
                                    final String currUserId = userId;
                                    Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
                                    final String postedBy = recipePostedBySet.iterator().next();
                                    DatabaseReference mFollow = mFirebaseDatabase.getReference();
                                    // If recipe is written by me (my recipe) then show
                                    if (currUserId.equals(postedBy)) {
                                        feedList.add(recipe);
                                        //adapter.add(recipe);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        // this recipe is not written by me - check if i follow this person
                                        mFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean display = false;
                                                boolean relevant = false;
                                                // Check if I follow this author and if my setting is only following relevant recipes
                                                if (dataSnapshot.child("/follows").hasChild(userId)) {
                                                    display = dataSnapshot.child("/follows").child(userId).hasChild(postedBy);
                                                    if (display) {
                                                        relevant = dataSnapshot.child("/follows").child(userId).child(postedBy).getValue(String.class).equals("relevant");
                                                    }
                                                }

                                                // Ingredient matching if I only follow relevant
                                                if (relevant) {
                                                    // First find my pantry
                                                    Pantry pantry = null;
                                                    for (DataSnapshot ds : dataSnapshot.child("/pantries").getChildren()) {
                                                        Pantry p = ds.getValue(Pantry.class);
                                                        if (p.getOwnedBy().containsKey(userId)) {
                                                            // this is user's pantry
                                                            pantry = p;
                                                            break;
                                                        }
                                                    }
                                                    // Get all items in pantry into a list
                                                    if (pantry == null || pantry.getItems() == null || pantry.getItems().size() == 0) {
                                                        // No items, do not display
                                                        display = false;
                                                    } else {
                                                        // Just get item strings for now
                                                        List<String> itemNames = new ArrayList<>();
                                                        Set<String> itemIds = pantry.getItems().keySet();
                                                        for (String itemId : itemIds) {
                                                            if (dataSnapshot.child("/items").hasChild(itemId)) {
                                                                Item i = dataSnapshot.child("/items").child(itemId).getValue(Item.class);
                                                                itemNames.add(i.getName());
                                                            }
                                                        }
                                                        // Now with a list of item names, compare with recipe's item names
                                                        if (!hasIngredients(recipe, itemNames)) {
                                                            display = false;
                                                        }
                                                    }
                                                }

                                                // Check if I'm in this group - if I am, this overrides follow/relevant settings for group recipes
                                                if (groupId != null) {
                                                    if (dataSnapshot.child("/group").hasChild(userId)) {
                                                        display = dataSnapshot.child("/group").child(userId).hasChild(groupId);
                                                    }
                                                }

                                                // Check if currUser has restrictions for this recipe
                                                User currUser = dataSnapshot.child("userAcccounts").child(userId).getValue(User.class);
                                                // Get a set of restrictions for user
                                                Set<String> restrictions;
                                                try {
                                                    restrictions = currUser.getRestrictions().keySet();
                                                } catch (Exception e) {
                                                    restrictions = null;
                                                }
                                                // Get restriction tags for the recipe
                                                List<String> tags = recipe.getTags();
                                                if (!meetsRestrictions(restrictions, tags)) {
                                                    display = false;
                                                }

                                                if (display) {
                                                    feedList.add(recipe);
                                                    adapter.notifyDataSetChanged();
                                                    log("Display_recipe", recipe.getDbKey());
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

    private boolean meetsRestrictions(Set<String> restrictions, List<String> tags) {
        // if everything in restrictions appears in recipe's tags, return true
        if (restrictions == null || tags == null || restrictions.size() == 0 || tags.size() == 0) {
            return false;
        } else {
            // For each restriction, make sure nothing in the tag matches
            for (String restriction : restrictions) {
                for (String tag : tags) {
                    if (!Pattern.compile(Pattern.quote(restriction), Pattern.CASE_INSENSITIVE).matcher(tag).find()) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean hasIngredients(Recipe recipe, List<String> itemNames) {
        for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
            String ingredientName = ingredient.getName();
            for (String itemName : itemNames) {
                if (!Pattern.compile(Pattern.quote(itemName), Pattern.CASE_INSENSITIVE).matcher(ingredientName).find()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void log(String eventType, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(eventType, value);
        mFirebaseAnalytics.logEvent("HomeFragment", bundle);
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
