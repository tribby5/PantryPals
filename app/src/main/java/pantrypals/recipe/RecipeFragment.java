package pantrypals.recipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pantrypals.models.Recipe;
import pantrypals.util.DownloadImageTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/recipes");

    private static final String ARG_RID = "RID";

    private String rid;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public static RecipeFragment newFragment(String rid) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RID, rid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.rid = getArguments().getString(ARG_RID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        final TextView nameTV = view.findViewById(R.id.recipe_name);
        final TextView captionTV = view.findViewById(R.id.recipe_caption);
        final TextView posterTV = view.findViewById(R.id.recipe_poster);
        final TextView scoreTV = view.findViewById(R.id.recipe_score);
        final ImageView imageView = view.findViewById(R.id.recipe_image);
        final LinearLayout instructionsLayout = view.findViewById(R.id.instructionsLayout);
        final LinearLayout ingredientsLayout = view.findViewById(R.id.ingredientsLayout);

        mDatabase.child(rid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                nameTV.setText(recipe.getName());
                captionTV.setText(recipe.getCaption());
                for(String user : recipe.getPostedBy().keySet()) {
                    posterTV.setText(user.substring(0, 10));
                }
                scoreTV.setText(recipe.getAverageRating());
                new DownloadImageTask(imageView)
                        .execute(recipe.getImageURL());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
