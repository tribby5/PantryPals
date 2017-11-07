package pantrypals.discover;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

public class DiscoverDetailFragment extends Fragment {

    private static final String ARG_TITLE = "ARG_TITLE";

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

        TextView detailTitle = view.findViewById(R.id.discover_detail_title);
        Bundle args = getArguments();
        CharSequence title = args.getCharSequence(ARG_TITLE);

        detailTitle.setText(title);

        return view;
    }

}
