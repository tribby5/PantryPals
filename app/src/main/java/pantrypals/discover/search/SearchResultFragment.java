package pantrypals.discover.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pantrypals.discover.GridAdapter;
import pantrypals.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultFragment extends Fragment {

    private static final String ARG_QUERY = "query";
    private static final String ARG_TYPE = "type";

    private static final String TAG = "SearchResultFragment";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private String query;
    private SearchType type;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(String query, SearchType type) {
        Log.d(TAG, "newInstance");
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        args.putString(ARG_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = SearchType.valueOf(getArguments().getString(ARG_TYPE));
            query = getArguments().getString(ARG_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        GridView gridView = view.findViewById(R.id.search_result_grid_view);

        populateAdapter(gridView);

        return view;
    }

    private void populateAdapter(final GridView gridView) {
        if(type == SearchType.PEOPLE) {
            Log.d(TAG, "populating adapter: " + query);
            mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<User> results = Lists.newArrayList();
                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if(user.getName().toLowerCase().contains(query)) {
                            results.add(user);
                        }
                    }
                    gridView.setAdapter(new SearchResultAdapter(getActivity(), results));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}