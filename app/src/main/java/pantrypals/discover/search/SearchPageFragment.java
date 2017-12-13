package pantrypals.discover.search;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import pantrypals.profile.ViewPagerAdapter;

/**
 * @author adityasrinivasan
 */
public class SearchPageFragment extends Fragment {

    private static final String ARG_QUERY = "query";

    private static final String TAG = "SearchPageFragment";

    private String query;

    public SearchPageFragment() {
        // Required empty public constructor
    }

    public static SearchPageFragment newInstance(String query) {
        SearchPageFragment fragment = new SearchPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_QUERY);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && getView() != null) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        TabLayout tabLayout = view.findViewById(R.id.search_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.search_view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());

        adapter.addTab(SearchResultFragment.newInstance(query, SearchType.ALL), "All");
        adapter.addTab(SearchResultFragment.newInstance(query, SearchType.PEOPLE), "People");
        adapter.addTab(SearchResultFragment.newInstance(query, SearchType.POSTS), "Recipes");
        adapter.addTab(SearchResultFragment.newInstance(query, SearchType.GROUPS), "Groups");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        ((TextView) view.findViewById(R.id.search_title)).setText("Searching for \"" + query + "\"");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        createSearchBar(menu);
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
