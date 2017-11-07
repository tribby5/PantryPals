package pantrypals.discover;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;


public class DiscoverFragment extends Fragment {

    private ItemClickListener mListener;

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

        

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ItemClickListener) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (ItemClickListener) activity;
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
            fp.setImageResource(ids[i]);
            layout.addView(fp);
        }
    }

    private void createSearchBar(Menu menu) {
        // Implementing ActionBar Search inside a fragment
        MenuItem item = menu.add("Search");
        item.setIcon(R.drawable.ic_discover); // sets icon
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());

        // modifying the text inside edittext component
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = sv.findViewById(id);
        textView.setHint("Search for people, posts, or communities...");
        textView.setHintTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
        textView.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));

        // implementing the listener
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
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
