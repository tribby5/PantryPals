package pantrypals.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.databaes.pantrypals.R;

import java.util.List;

import pantrypals.models.Recipe;
import pantrypals.recipe.RecipeListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedForLaterFragment extends Fragment {

    private static List<Recipe> recipes;

    public SavedForLaterFragment() {
        // Required empty public constructor
    }

    public static SavedForLaterFragment newFragment(List<Recipe> recipes) {
        SavedForLaterFragment.recipes = recipes;
        return new SavedForLaterFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_for_later, container, false);

        LinearLayout ll = view.findViewById(R.id.saved_for_later_layout);
        ll.addView(RecipeListView.newInstance(getContext(), recipes));

        return view;
    }

}
