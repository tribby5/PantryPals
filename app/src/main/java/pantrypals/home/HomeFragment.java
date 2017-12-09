package pantrypals.home;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    //private ArrayList<Post> feedList = new ArrayList<>();
    private ArrayList<TempRecipe> feedList = new ArrayList<>();
    // getActivity for fragment
    private CustomListAdapter adapter;
    private String oldestPostId;

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
        ref.limitToFirst(2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals(oldestPostId)) {
                        oldestPostId = snapshot.getKey();
                        dataSnapshot.getChildrenCount();
                        TempRecipe recipe = snapshot.getValue(TempRecipe.class);
                        String tempRecipeId = snapshot.getKey();
                        // Remove this line
                        recipe.setImgURL("http://locations.in-n-out.com/Content/images/Combo.png");
                        //feedList.add(recipe);
                        if (meetsCondition(recipe)) {
                            adapter.add(recipe);
                        }
                        Log.d(TAG, "Retrieved Id: " + tempRecipeId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Inflate the recipe_layout first
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
                    ref.orderByKey().startAt(oldestPostId).limitToFirst(2)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (!snapshot.getKey().equals(oldestPostId)) {
                                            oldestPostId = snapshot.getKey();
                                            String tempRecipeId = snapshot.getKey();
                                            TempRecipe recipe = snapshot.getValue(TempRecipe.class);
                                            // Take out this line if url is there
                                            recipe.setImgURL("http://locations.in-n-out.com/Content/images/Combo.png");
                                            //feedList.add(recipe);
                                            if (meetsCondition(recipe)) {
                                                adapter.add(recipe);
                                            }
                                            Log.d(TAG, "Retrieved Id on Scroll: " + tempRecipeId);
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


        // Define click actions
//        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                if (position == 0) {
//
//                }
//            }
//        });
        return view;
    }

    /**
     * Add logic for intelligent feed filtering
     *
     * @param recipe
     * @return
     */
    private boolean meetsCondition(TempRecipe recipe) {
        return true;
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
