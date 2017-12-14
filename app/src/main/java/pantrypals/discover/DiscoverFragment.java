package pantrypals.discover;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pantrypals.discover.search.SearchPageFragment;
import pantrypals.models.Group;
import pantrypals.models.Recipe;
import pantrypals.util.DownloadImageTask;


public class DiscoverFragment extends Fragment {

    private static final String TAG = "DiscoverFragment";
    private FirebaseAnalytics mFirebaseAnalytics;

    // Temporary: hardcoding results until database has correct data
    private static final List<String> ITEMS = Lists.newArrayList("Trending", "Moods", "Cuisines", "Communities");

    private DiscoverItemClickListener mListener;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private LinearLayout featured_posts;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        return fragment;
    }

    /*
     * Overridden methods
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateTrendingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        populateFeaturedPosts(view);

        populateListView(view);

        return view;
    }

    private void populateListView(View view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_icon, ITEMS);
        ListView listView = view.findViewById(R.id.discover_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                mListener.discoverItemClicked(item);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (DiscoverItemClickListener) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (DiscoverItemClickListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        createSearchBar(menu);
    }

    /*
     * Helper methods
     */

    private void populateFeaturedPosts(View view) {
        featured_posts = view.findViewById(R.id.featured_posts);
        mDatabase.child("featured").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> featured = Lists.newArrayList();

                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String currRecipe = child.getKey();
                    featured.add(currRecipe);
                }

                for (String id : featured) {
                    mDatabase.child("recipes").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Recipe recipe = dataSnapshot.getValue(Recipe.class);
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            //View view = inflater.inflate(R.layout.grid_item, featured_posts, false);
                            // Instead of text view, use square layout code. Set image to recipe image

                            View view = inflater.inflate(R.layout.grid_item, null);

                            SquareLayout sq = view.findViewById(R.id.grid_item_square);
                            ImageView iv = view.findViewById(R.id.grid_item_image);
                            //String iconName = string.split(" ")[0].toLowerCase();
                            //int resID = mContext.getResources().getIdentifier(iconName , "drawable", mContext.getPackageName());
                            new DownloadImageTask(iv).execute(recipe.getImageURL());
                            iv.getLayoutParams().height = 200;
                            iv.getLayoutParams().width = 200;
                            iv.requestLayout();
                            TextView tv = view.findViewById(R.id.grid_item_text);
                            tv.setText(recipe.getName());
                            featured_posts.addView(sq);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void createSearchBar(Menu menu) {
        // Implementing ActionBar Search inside a fragment
        MenuItem item = menu.add("Search");
        item.setIcon(R.drawable.ic_discover);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());

        // Modify text in search bar
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = sv.findViewById(id);
        textView.setHint("Search PantryPals...");
        textView.setHintTextColor(getResources().getColor(R.color.colorHint));
        textView.setTextColor(getResources().getColor(R.color.colorWhite));

        // Implement the listener
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // log search
                log(FirebaseAnalytics.Event.SEARCH, "Search text: " + s);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, SearchPageFragment.newInstance(s.toLowerCase())).addToBackStack(null).commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // TODO: search on query
                return true;
            }
        });
        item.setActionView(sv);
    }

    private void log(String eventType, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(eventType, value);
        mFirebaseAnalytics.logEvent("HomeFragment", bundle);
    }
    private void generateTrendingData() {
        mDatabase.child("trending").setValue(null);

        // generate trending users
        mDatabase.child("userAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot1) {
                mDatabase.child("follows").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {
                        Map<String, Integer> followMap = Maps.newHashMap();
                        for(DataSnapshot userSnapshot1 : dataSnapshot1.getChildren()) {
                            final String userID = userSnapshot1.getKey();
                            int num = 0;
                            for (DataSnapshot userSnapshot2 : dataSnapshot2.getChildren()) {
                                for (DataSnapshot followingSnapshot : userSnapshot2.getChildren()) {
                                    if (followingSnapshot.getKey().equals(userID)) {
                                        num++;
                                    }
                                }
                            }
                            followMap.put(userID, num);
                        }
                        Ordering<Map.Entry<String, Integer>> entryOrdering = Ordering.natural()
                                .onResultOf(new Function<Map.Entry<String, Integer>, Integer>() {
                                    public Integer apply(Map.Entry<String, Integer> entry) {
                                        return entry.getValue();
                                    }
                                }).reverse();

                        // Desired entries in desired order.  Put them in an ImmutableMap in this order.
                        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
                        for (Map.Entry<String, Integer> entry :
                                entryOrdering.sortedCopy(followMap.entrySet())) {
                            builder.put(entry.getKey(), entry.getValue());
                        }
                        UnmodifiableIterator<String> sortedIter = builder.build().keySet().iterator();
                        for(int i = 0; i < 5 && sortedIter.hasNext(); i++) {
                            mDatabase.child("trending").child(sortedIter.next() + "@person").setValue(true);
                        }
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

        // generate trending groups
        mDatabase.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Map<String, Integer> groupMap = Maps.newHashMap();
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    groupMap.put(groupSnapshot.getKey(), group.getMembers().size());
                }
                Ordering<Map.Entry<String, Integer>> entryOrdering = Ordering.natural()
                        .onResultOf(new Function<Map.Entry<String, Integer>, Integer>() {
                            public Integer apply(Map.Entry<String, Integer> entry) {
                                return entry.getValue();
                            }
                        }).reverse();

                // Desired entries in desired order.  Put them in an ImmutableMap in this order.
                ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
                for (Map.Entry<String, Integer> entry :
                        entryOrdering.sortedCopy(groupMap.entrySet())) {
                    builder.put(entry.getKey(), entry.getValue());
                }
                UnmodifiableIterator<String> sortedIter = builder.build().keySet().iterator();
                for(int i = 0; i < 5 && sortedIter.hasNext(); i++) {
                    mDatabase.child("trending").child(sortedIter.next() + "@group").setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // generate trending groups
        mDatabase.child("recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Map<String, Double> recipeMap = Maps.newHashMap();
                for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    double rating;
                    try {
                        rating = Double.parseDouble(recipe.getAverageRating());
                    } catch(Exception e) {
                        try {
                            rating = Integer.parseInt(recipe.getAverageRating());
                        } catch(Exception ee) {
                            rating = 0.0;
                        }
                    }
                    recipeMap.put(recipeSnapshot.getKey(), rating * -recipe.getNegTimestamp());
                }
                Ordering<Map.Entry<String, Double>> entryOrdering = Ordering.natural()
                        .onResultOf(new Function<Map.Entry<String, Double>, Double>() {
                            public Double apply(Map.Entry<String, Double> entry) {
                                return entry.getValue();
                            }
                        }).reverse();

                // Desired entries in desired order.  Put them in an ImmutableMap in this order.
                ImmutableMap.Builder<String, Double> builder = ImmutableMap.builder();
                for (Map.Entry<String, Double> entry :
                        entryOrdering.sortedCopy(recipeMap.entrySet())) {
                    builder.put(entry.getKey(), entry.getValue());
                }
                UnmodifiableIterator<String> sortedIter = builder.build().keySet().iterator();
                for(int i = 0; i < 5 && sortedIter.hasNext(); i++) {
                    mDatabase.child("trending").child(sortedIter.next() + "@recipe").setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

