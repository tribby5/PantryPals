package pantrypals.discover;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;

import java.util.List;

import pantrypals.discover.search.SearchPageFragment;


public class DiscoverFragment extends Fragment {

    private static final String TAG = "DiscoverFragment";

    // Temporary: hardcoding results until database has correct data
    private static final List<String> ITEMS = Lists.newArrayList("Trending", "Moods", "Cuisines", "Communities");

    private DiscoverItemClickListener mListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

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
        LinearLayout layout = view.findViewById(R.id.featured_posts);
        int[] ids = {R.drawable.ic_discover, R.drawable.ic_home, R.drawable.ic_notifications, R.drawable.ic_pantry, R.drawable.ic_person};
        for (int i = 0; i < 5; i++) {
            FeaturedPost fp = new FeaturedPost(getContext());
            fp.setPadding(50, 50, 50, 50);
            fp.setBackgroundColor(i * 20);
            fp.setImageResource(ids[i]);
            layout.addView(fp);
        }
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
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, SearchPageFragment.newInstance(s)).commit();
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


}
