package pantrypals.discover;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;


public class DiscoverFragment extends Fragment {

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

        setItemClickListeners(view);

        return view;
    }

    private void setItemClickListeners(View view) {
        TextView trending = view.findViewById(R.id.discover_list_trending_item);
        setItemClickListener(trending);
        TextView moods = view.findViewById(R.id.discover_list_moods_item);
        setItemClickListener(moods);
        TextView cuisines = view.findViewById(R.id.discover_list_cuisines_item);
        setItemClickListener(cuisines);
        TextView communities = view.findViewById(R.id.discover_list_communities_item);
        setItemClickListener(communities);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setItemClickListener(final TextView item) {
        item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    item.setBackgroundResource(R.color.colorWhite);
                    mListener.discoverItemClicked(item.getText());
                }
                item.setBackgroundResource(R.color.colorHint);
                return true;
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
