package pantrypals.recipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wefika.flowlayout.FlowLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import pantrypals.models.Recipe;
import pantrypals.models.User;
import pantrypals.profile.ProfileFragment;
import pantrypals.util.DownloadImageTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
        final TextView dateTV = view.findViewById(R.id.recipe_date);
        final ImageView imageView = view.findViewById(R.id.recipe_image);
        final List<ImageView> stars = Lists.newArrayList(
                (ImageView) view.findViewById(R.id.recipe_star1),
                (ImageView) view.findViewById(R.id.recipe_star2),
                (ImageView) view.findViewById(R.id.recipe_star3),
                (ImageView) view.findViewById(R.id.recipe_star4),
                (ImageView) view.findViewById(R.id.recipe_star5));

        final int activeStarID = R.drawable.star;
        final int inactiveStarID = R.drawable.star_tint;

        final LinearLayout instructionsLayout = view.findViewById(R.id.instructionsLayout);
        final LinearLayout ingredientsLayout = view.findViewById(R.id.ingredientsLayout);
        final FlowLayout tagLayout = view.findViewById(R.id.recipe_tags_container);

        mDatabase.child("recipes").child(rid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                new DownloadImageTask(imageView)
                        .execute(recipe.getImageURL());
                nameTV.setText(recipe.getName());
                captionTV.setText(recipe.getCaption());
                for(final String userID : recipe.getPostedBy().keySet()) {
                    mDatabase.child("userAccounts").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            posterTV.setText(user.getName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    posterTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProfileFragment newFragment = ProfileFragment.newFragment(userID);
                            if(isAdded()) {
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, newFragment).commit();
                            }
                        }
                    });
                }
                double rating;
                try {
                    rating = Double.parseDouble(recipe.getAverageRating());
                } catch (NullPointerException e) {
                    rating = 0.0;
                }
                for(int i = 0; i < stars.size(); i++) {
                    if ((rating / (i + 1)) >= 1) {
                        stars.get(i).setImageResource(activeStarID);
                    } else {
                        stars.get(i).setImageResource(inactiveStarID);
                    }
                }
                String date;
                try {
                    date = unixToDate(recipe.getNegTimestamp());
                } catch (ParseException pe) {
                    date = "Unknown date";
                }
                dateTV.setText(date);

                List<Recipe.Ingredient> ingredients = recipe.getIngredients();

                for(Recipe.Ingredient ingredient : ingredients) {
                    if(isAdded()) {
                        TextView ingTV = new TextView(getContext());
                        if (ingredient.getUnit() != null) {
                            ingTV.setText(String.format(Locale.US, "• %d %s %s", (int) ingredient.getAmount(), ingredient.getUnit(), ingredient.getName()));
                        } else {
                            ingTV.setText(String.format(Locale.US, "• %d %s", (int) ingredient.getAmount(), ingredient.getName()));
                        }
                        ingTV.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
                        ingTV.setPadding(0, 20, 0, 20);
                        ingredientsLayout.addView(ingTV);
                    }
                }

                List<String> instructions = recipe.getInstructions();

                for(int i = 1; i <= instructions.size(); i++) {
                    if(isAdded()) {
                        TextView instrTV = new TextView(getContext());
                        instrTV.setText(String.format(Locale.US, "%d. %s", i, instructions.get(i - 1)));
                        instrTV.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
                        instrTV.setPadding(0, 20, 0, 20);
                        instructionsLayout.addView(instrTV);
                    }
                }

                List<String> tags = recipe.getTags();

                tagLayout.removeAllViews();
                if (tags != null && tags.size() != 0) {
                    for(String tag : tags) {
                        if(isAdded()) {
                            TextView tagTV = new TextView(getContext());
                            tagTV.setText(tag);
                            tagTV.setTextSize(12);
                            tagTV.setPadding(25, 15, 25, 15);
                            if (isAdded()) {
                                tagTV.setTextColor(getResources().getColor(R.color.colorWhite));
                                tagTV.setBackground(getResources().getDrawable(R.drawable.rounded_corner_blue));
                            }

                            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(10, 0, 10, 0);

                            tagTV.setLayoutParams(params);
                            tagLayout.addView(tagTV);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private String unixToDate(long unix_timestamp) throws ParseException {
        long timestamp = unix_timestamp * -1000;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d H:mm", Locale.US);
        return sdf.format(timestamp);
    }

}
