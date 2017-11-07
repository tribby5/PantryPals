package pantrypals.pantry;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databaes.pantrypals.R;


public class PantryFragment extends Fragment {

    public PantryFragment() {
        // Required empty public constructor
    }

    public static PantryFragment newInstance() {
        PantryFragment fragment = new PantryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the recipe_layout for this fragment
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }


}
