package pantrypals.recipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.io.Serializable;
import java.util.List;

import pantrypals.models.Recipe;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListFragment extends Fragment {

    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_RECIPES = "RECIPES";

    private List<Recipe> recipes;
    private String title;

    public RecipeListFragment() {
        // Required empty public constructor
    }

    public static RecipeListFragment newFragment(String title, List<Recipe> recipes) {
        RecipeListFragment fragment = new RecipeListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_RECIPES, (Serializable) recipes);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.recipes = (List<Recipe>) getArguments().getSerializable(ARG_RECIPES);
            this.title = getArguments().getString(ARG_TITLE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        TextView titleTV = view.findViewById(R.id.recipe_list_title);
        titleTV.setText(title);

        LinearLayout ll = view.findViewById(R.id.recipe_list_layout);
        ll.addView(RecipeListView.newInstance(getContext(), recipes));

        return view;
    }

}
