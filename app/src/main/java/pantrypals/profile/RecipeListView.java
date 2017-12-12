package pantrypals.profile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.databaes.pantrypals.R;

import java.util.List;

import pantrypals.home.CustomListAdapter;
import pantrypals.models.Recipe;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListView extends ListView {

    public RecipeListView(Context context) {
        super(context);
    }

    public static ListView newInstance(Context context, List<Recipe> recipes) {
        ListView recipeListView = new RecipeListView(context);

        CustomListAdapter adapter = new CustomListAdapter(context, R.layout.card_layout_main, recipes);
        recipeListView.setAdapter(adapter);

        return recipeListView;
    }


}
