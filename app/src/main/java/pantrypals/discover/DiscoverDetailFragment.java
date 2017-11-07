package pantrypals.discover;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class DiscoverDetailFragment extends Fragment {

    private static final String ARG_TITLE = "ARG_TITLE";

    // Temporary: hardcoding results until database has correct data
    private static final Map<String, List<String>> MAP = Maps.newHashMap();

    static {
        MAP.put("Moods", Lists.newArrayList("Comfort", "Breakfast", "Healthy", "Lazy", "Date night", "Sweet tooth"));
        MAP.put("Cuisines", Lists.newArrayList(""))
    }

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

        setTitle(view);
        populateGridView(view);

        return view;
    }

    private void setTitle(View view) {
        TextView detailTitle = view.findViewById(R.id.discover_detail_title);
        Bundle args = getArguments();
        CharSequence title = args.getCharSequence(ARG_TITLE);

        detailTitle.setText(title);
    }

    private void populateGridView(View view) {
        GridView gridView = view.findViewById(R.id.discover_detail_grid_view);
        GridAdapter adapter = new GridAdapter(getActivity(), MAP.get(getArguments().getCharSequence(ARG_TITLE)));
        gridView.setAdapter(adapter);
    }

}
