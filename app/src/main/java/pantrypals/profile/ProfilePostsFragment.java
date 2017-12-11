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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;

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
//        // Inflate the layout for this fragment
//        userId = FirebaseAuth.getInstance().getCurrentUser().user.getUid();
//
//        // Retrieve data from Firebase
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/recipes");
//
//        ref.orderByChild("negTimestamp").limitToFirst(4).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Recipe recipe = snapshot.getValue(Recipe.class);
//                    if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
//                        oldestRecipeNegTimestamp = recipe.getNegTimestamp();
//
//                        String recipeId = snapshot.getKey();
//
//                        recipe.setDbKey(recipeId);
//                        //feedList.add(recipe);
//                        if (meetsCondition(recipe)) {
//                            adapter.add(recipe);
//                        }
//                        Log.d(TAG, "Retrieved Id: " + recipeId);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        // Inflate the recipe_layout first
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        // newPostButton
//        Button newPostButton = (Button) view.findViewById(R.id.newPostButton);
//        newPostButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent postIntent = new Intent(getActivity(), NewRecipeActivity.class);
//                startActivity(postIntent);
//            }
//        });
//
//        feedListView = (ListView) view.findViewById(R.id.feedListView);
//        adapter = new CustomListAdapter(getActivity(), R.layout.card_layout_main, feedList);
//        feedListView.setAdapter(adapter);
//
//
//        // Implement scrolling
//        feedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            private int currentVisibleItemCount;
//            private int currentScrollState;
//            private int currentFirstVisibleItem;
//            private int totalItem;
//            private LinearLayout lBelow;
//
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                currentScrollState = scrollState;
//                isScrollCompleted();
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                currentFirstVisibleItem = firstVisibleItem;
//                currentVisibleItemCount = visibleItemCount;
//                totalItem = totalItemCount;
//            }
//
//            private void isScrollCompleted() {
//                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
//                        && currentScrollState == SCROLL_STATE_IDLE) {
//                    ref.orderByChild("negTimestamp").startAt(oldestRecipeNegTimestamp).limitToFirst(4)
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                        Recipe recipe = snapshot.getValue(Recipe.class);
//                                        if (recipe.getNegTimestamp() != oldestRecipeNegTimestamp) {
//                                            oldestRecipeNegTimestamp = recipe.getNegTimestamp();
//                                            String recipeId = snapshot.getKey();
//                                            // Take out this line if url is there
//                                            //recipe.setImgURL(TEMP_IMAGE);
//                                            recipe.setDbKey(recipeId);
//                                            //feedList.add(recipe);
//                                            if (meetsCondition(recipe)) {
//                                                adapter.add(recipe);
//                                            }
//                                            Log.d(TAG, "Retrieved Id on Scroll: " + recipeId);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                }
//            }
//        });
        return view;
    }

}
