package pantrypals.discover;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

import pantrypals.discover.search.SearchResult;
import pantrypals.discover.search.SearchResultAdapter;
import pantrypals.discover.search.SearchType;
import pantrypals.home.CustomListAdapter;
import pantrypals.models.Group;
import pantrypals.models.Post;
import pantrypals.models.Recipe;
import pantrypals.models.User;
import pantrypals.recipe.RecipeListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverResultFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static final String ARG_TITLE = "title";
    private static final String ARG_RESULTS = "results";

    private String title;
    private List<SearchResult> results;

    public DiscoverResultFragment() {
        // Required empty public constructor
    }

    public static DiscoverResultFragment newInstance(String title, List<SearchResult> results) {
        DiscoverResultFragment fragment = new DiscoverResultFragment();
        Bundle args = new Bundle();

        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_RESULTS, (Serializable) results);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            results = (List<SearchResult>) getArguments().getSerializable(ARG_RESULTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_result, container, false);

        TextView titleTV = view.findViewById(R.id.discover_result_title);
        titleTV.setText(title);

        LinearLayout ll = view.findViewById(R.id.discover_result_layout);
        ListView lv = view.findViewById(R.id.discover_result_lv);
        lv.setAdapter(new SearchResultAdapter(getContext(), results));

        return view;
    }

}
