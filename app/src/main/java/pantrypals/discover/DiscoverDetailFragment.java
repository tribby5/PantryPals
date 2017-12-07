package pantrypals.discover;


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
import com.google.common.collect.Maps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class DiscoverDetailFragment extends Fragment {

    private static final String ARG_TITLE = "ARG_TITLE";

    // Temporary: hardcoding results until database has correct data
    private static final Map<String, List<String>> MAP = Maps.newHashMap();

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private GridView gridView;

    public DiscoverDetailFragment() {
        // Required empty public constructor
    }

    public static DiscoverDetailFragment newFragment(CharSequence title) {
        DiscoverDetailFragment fragment = new DiscoverDetailFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_detail, container, false);

        gridView = view.findViewById(R.id.discover_detail_grid_view);

        setTitle(view);

        mDatabase.child("/" + ((String) getArguments().get(ARG_TITLE)).toLowerCase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> categories = Lists.newArrayList();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    categories.add(child.getKey());
                }
                GridAdapter adapter = new GridAdapter(getActivity(), categories);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void setTitle(View view) {
        TextView detailTitle = view.findViewById(R.id.discover_detail_title);
        Bundle args = getArguments();
        CharSequence title = args.getCharSequence(ARG_TITLE);

        detailTitle.setText(title);
    }

}
