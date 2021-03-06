package pantrypals.recipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.wefika.flowlayout.FlowLayout;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import pantrypals.models.GroceryItem;
import pantrypals.models.Notification;
import pantrypals.models.Recipe;
import pantrypals.models.User;
import pantrypals.profile.ProfileFragment;
import pantrypals.util.AuthUserInfo;
import pantrypals.util.DownloadImageTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static final String ARG_RID = "RID";

    private boolean mLock = false;

    private boolean mProcessRateNotif = false;

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
        final Button addToGroceryListButton = view.findViewById(R.id.addToGroceryList);
        final FlowLayout tagLayout = view.findViewById(R.id.recipe_tags_container);

        mLock = true;

        mDatabase.child("recipes").child(rid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if(mLock) {

                    // Add a click Action
                    addToGroceryListButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
                                String name;
                                String unit;
                                double amount;
                                if (ingredient.getName() != null) {
                                    name = ingredient.getName();
                                } else {
                                    continue;
                                }
                                if (ingredient.getUnit() != null) {
                                    unit = ingredient.getUnit();
                                } else {
                                    unit = "";
                                }
                                try {
                                    amount = ingredient.getAmount();
                                } catch (Exception e) {
                                    continue;
                                }

                                GroceryItem item = new GroceryItem();
                                item.setName(name);
                                item.setAmount(amount);
                                item.setUnit(unit);

                                String currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String newItemKey = mDatabase.child("groceryLists").child(currUserId).push().getKey();
                                mDatabase.child("groceryLists").child(currUserId).child(newItemKey).setValue(item);

                            }
                            toastMessage("Ingredients added to My Grocery List.");
                        }
                    });


                    new DownloadImageTask(imageView)
                            .execute(recipe.getImageURL());
                    nameTV.setText(recipe.getName());
                    captionTV.setText(recipe.getCaption());
                    for (final String userID : recipe.getPostedBy().keySet()) {
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
                                if (isAdded()) {
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_layout, newFragment).addToBackStack(null).commit();
                                }
                            }
                        });
                    }
                }
                double rating = 0.0;
                try {
                    if(recipe.getRatings() == null || recipe.getRatings().isEmpty()) {
                        rating = Double.parseDouble(recipe.getAverageRating());
                    } else {
                        for(int val : recipe.getRatings().values()) {
                            rating += (double) val;
                        }
                        rating /= recipe.getRatings().size();
                    }
                } catch (Exception e) {
                    rating = 0.0;
                }
                for(int i = 0; i < stars.size(); i++) {
                    if ((rating / (i + 1)) >= 1) {
                        stars.get(i).setImageResource(activeStarID);
                    } else {
                        stars.get(i).setImageResource(inactiveStarID);
                    }
                }
                for(int i = 0; i < stars.size(); i++) {
                    final int score = i + 1;
                    stars.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProcessRateNotif = true;
                            Map<String, Integer> ratingMap = recipe.getRatings();
                            if(ratingMap == null) {
                                ratingMap = Maps.newHashMap();
                            }
                            ratingMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), score);
                            recipe.setRatings(ratingMap);
                            mDatabase.child("recipes").child(dataSnapshot.getKey()).setValue(recipe);
                            final String notifID = UUID.randomUUID().toString().substring(0, 32).replace("-", "");
                            final Notification notif = new Notification();
                            notif.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
                            notif.setLinkType("recipe");
                            notif.setImageURL(recipe.getImageURL());
                            notif.setLinkID(dataSnapshot.getKey());
                            notif.setOriginator(recipe.getName());
                            notif.setMessage(AuthUserInfo.INSTANCE.getUser().getName() + " has rated this recipe " + score + " stars.");
                            mDatabase.child("notifications").child(notifID).setValue(notif);
                            mDatabase.child("userAccounts").child(recipe.getPostedBy().keySet().iterator().next()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(mProcessRateNotif) {
                                        User user = dataSnapshot.getValue(User.class);
                                        Map<String, Boolean> notifMap = user.getNotifications();
                                        if (notifMap == null) {
                                            notifMap = Maps.newHashMap();
                                        }
                                        notifMap.put(notifID, true);
                                        user.setNotifications(notifMap);
                                        mDatabase.child("userAccounts").child(recipe.getPostedBy().keySet().iterator().next()).setValue(user);
                                    }
                                    mProcessRateNotif = false;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(getContext(), "Rated " + (score + 1) + " stars!", Toast.LENGTH_SHORT);
                        }
                    });
                }
                if(mLock) {
                    String date;
                    try {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date dt;
                        dt = dateFormat.parse(recipe.getTimePosted());
                        SimpleDateFormat newFormat = new SimpleDateFormat("MMM d, yyyy,  hh:mm aaa");
                        date = newFormat.format(dt);
                        // below: NOT UNIX TIMESTAMP!!
                        //date = unixToDate(recipe.getNegTimestamp());

                    } catch (ParseException pe) {
                        date = "Unknown date";
                    }
                    dateTV.setText(date);

                    List<Recipe.Ingredient> ingredients = recipe.getIngredients();

                    for (Recipe.Ingredient ingredient : ingredients) {
                        if (isAdded()) {
                            TextView ingTV = new TextView(getContext());
                            double amt = ingredient.getAmount() == 0d ? 1d : ingredient.getAmount();
                            if (ingredient.getUnit() != null) {
                                ingTV.setText(String.format(Locale.US, "• %.2f %s %s", amt, ingredient.getUnit(), ingredient.getName()));
                            } else {
                                ingTV.setText(String.format(Locale.US, "• %.2f %s", amt, ingredient.getName()));
                            }
                            ingTV.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
                            ingTV.setPadding(0, 20, 0, 20);
                            ingredientsLayout.addView(ingTV);
                        }
                    }

                    List<String> instructions = recipe.getInstructions();

                    for (int i = 1; i <= instructions.size(); i++) {
                        if (isAdded()) {
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
                        for (String tag : tags) {
                            if (isAdded()) {
                                TextView tagTV = new TextView(getContext());
                                tagTV.setText(tag);
                                tagTV.setTextSize(12);
                                tagTV.setPadding(25, 15, 25, 15);
                                if (isAdded()) {
                                    tagTV.setTextColor(getResources().getColor(R.color.colorWhite));
                                    tagTV.setBackground(getResources().getDrawable(R.drawable.rounded_corner_blue));
                                }

                                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(10, 10, 10, 10);

                                tagTV.setLayoutParams(params);
                                tagLayout.addView(tagTV);
                            }
                        }
                    }
                }
                mLock = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
