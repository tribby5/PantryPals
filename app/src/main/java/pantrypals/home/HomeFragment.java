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

    private static final String TEMP_IMAGE = "https://metrouk2.files.wordpress.com/2017/10/523733805-e1508406361613.jpg";
    private static final String TAG = "HomeFragment";

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    private boolean bool = false;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String userId;

    private ListView feedListView;
    private ArrayList<Recipe> feedList = new ArrayList<>();
    // getActivity for fragment
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

        // Retrieve data from Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        ref = mFirebaseDatabase.getReference("/recipes");

//        //TEMP CODE TO ADD NEGTIMESTAMP FIELD TO ALL RECIPE ENTRIES
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Recipe r = snapshot.getValue(Recipe.class);
//                    String rid = snapshot.getKey();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                    long negts = 0;
//                    try {
//                        Date dt = dateFormat.parse(r.getTimePosted());
//                        negts = dt.getTime() * -1;
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    ref.child(rid).child("negTimestamp").setValue(negts);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        ref.orderByChild("negTimestamp").limitToFirst(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
                        oldestRecipeNegTimestamp = recipe.getNegTimestamp();
                        //dataSnapshot.getChildrenCount();

                        String recipeId = snapshot.getKey();
                        // Remove this line
                        //recipe.setImgURL("http://locations.in-n-out.com/Content/images/Combo.png");
                        recipe.setDbKey(recipeId);
                        //feedList.add(recipe);

                        // filter based on whether i follow this person or not
                        final String currUserId = mAuth.getCurrentUser().getUid();
                        Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
                        final String postedBy = recipePostedBySet.iterator().next();
                        DatabaseReference mFollow = FirebaseDatabase.getInstance().getReference("/follows");
                        boolean isFollowed = false;
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
            private LinearLayout lBelow;

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
                    ref.orderByChild("negTimestamp").startAt(oldestRecipeNegTimestamp).limitToFirst(4)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final Recipe recipe = snapshot.getValue(Recipe.class);
                                        if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
                                            oldestRecipeNegTimestamp = recipe.getNegTimestamp();
                                            String recipeId = snapshot.getKey();
                                            // Take out this line if url is there
                                            //recipe.setImgURL(TEMP_IMAGE);
                                            recipe.setDbKey(recipeId);
                                            //feedList.add(recipe);

                                            // filter based on whether i follow this person or not
                                            final String currUserId = mAuth.getCurrentUser().getUid();
                                            Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
                                            final String postedBy = recipePostedBySet.iterator().next();
                                            DatabaseReference mFollow = FirebaseDatabase.getInstance().getReference("/follows");
                                            boolean isFollowed = false;
                                            mFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    boolean value = dataSnapshot.child(currUserId).hasChild(postedBy);
                                                    if (dataSnapshot.child(currUserId).hasChild(postedBy)) {
                                                        adapter.add(recipe);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });




                                            Log.d(TAG, "Retrieved Id on Scroll: " + recipeId);
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

//    /**
//     * Add logic for intelligent feed filtering
//     *
//     * @param recipe
//     * @return
//     */
//    private boolean meetsCondition(Recipe recipe) {
//
//
//
//        // filter based on whether i follow this person or not
//        final String currUserId = mAuth.getCurrentUser().getUid();
//        Set<String> recipePostedBySet = recipe.getPostedBy().keySet();
//        final String postedBy = recipePostedBySet.iterator().next();
//        DatabaseReference mFollow = FirebaseDatabase.getInstance().getReference("/follows");
//        mFollow.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                boolean mybool = dataSnapshot.child(currUserId).hasChild(postedBy);
//                if (mybool) {
//                    // TODO: Find out whether I have the ingredients here
//                    setBool(true);
//                } else {
//                    setBool(false);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        Boolean bb = getBool();
//
//        return getBool();
//    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
