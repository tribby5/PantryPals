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
import pantrypals.models.Group;
import pantrypals.models.Post;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        GridView gridView = view.findViewById(R.id.search_result_grid_view);

        populateAdapter(gridView);

        return view;
    }

    private void populateAdapter(final GridView gridView) {
        if(type == SearchType.PEOPLE) {
            mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<SearchResult> results = Lists.newArrayList();
                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if(query.equals("*") || user.getName().toLowerCase().contains(query)) {
                            results.add(new SearchResult(type, user, userSnapshot.getKey()));
                        }
                    }
                    gridView.setAdapter(new SearchResultAdapter(getActivity(), results));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if(type == SearchType.GROUPS) {
            mDatabase.child("/groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<SearchResult> results = Lists.newArrayList();
                    for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        Group group = groupSnapshot.getValue(Group.class);
                        if(query.equals("*") || group.getName().toLowerCase().contains(query)) {
                            results.add(new SearchResult(type, group, groupSnapshot.getKey()));
                        }
                    }
                    gridView.setAdapter(new SearchResultAdapter(getActivity(), results));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if(type == SearchType.POSTS) {
            mDatabase.child("/posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<SearchResult> results = Lists.newArrayList();
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if(query.equals("*") || post.getTitle().toLowerCase().contains(query)) {
                            results.add(new SearchResult(type, post, postSnapshot.getKey()));
                        }
                    }
                    gridView.setAdapter(new SearchResultAdapter(getActivity(), results));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if(type == SearchType.ALL) {
            mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<SearchResult> results = Lists.newArrayList();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (query.equals("*") || user.getName().toLowerCase().contains(query)) {
                            results.add(new SearchResult(type, user, userSnapshot.getKey()));
                        }
                    }
                    mDatabase.child("/groups").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                                Group group = groupSnapshot.getValue(Group.class);
                                if (query.equals("*") || group.getName().toLowerCase().contains(query)) {
                                    results.add(new SearchResult(type, group, groupSnapshot.getKey()));
                                }
                            }
                            mDatabase.child("/posts").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Post post = postSnapshot.getValue(Post.class);
                                        if(query.equals("*") || post.getTitle().toLowerCase().contains(query)) {
                                            results.add(new SearchResult(type, post, postSnapshot.getKey()));
                                        }
                                    }
                                    gridView.setAdapter(new SearchResultAdapter(getActivity(), results));
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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
