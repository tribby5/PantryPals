package pantrypals.home;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.ArrayAdapter;

/**
 * Created by Hunter Lee.
 */


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Maps;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import pantrypals.models.Item;
import pantrypals.models.Notification;
import pantrypals.models.Pantry;
import pantrypals.models.Recipe;
import pantrypals.models.TempRecipe;
import pantrypals.models.User;
import pantrypals.recipe.RecipeFragment;
import pantrypals.util.AuthUserInfo;


public class CustomListAdapter extends ArrayAdapter<Recipe> {

    @Override
    public int getCount() {
        return objects.size();
    }

    private static final String TAG = "CustomListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private boolean mProcessLike = false;
    private boolean mProcessSave = false;
    private boolean mAddLikeNotifToUser = false;
    private boolean mProcessCardBullets = false;
    private DatabaseReference refLike;
    private DatabaseReference refSave;
    private DatabaseReference refRoot;
    private DatabaseReference refAuthor;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth mAuth;
    private FragmentManager fm;
    private List<Recipe> objects;

    /**
     * Default constructor for the PersonListAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public CustomListAdapter(Context context, int resource, List<Recipe> objects, FragmentActivity activity) {
        super(context, resource, objects);
        if (activity != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        } else {
            mFirebaseAnalytics = null;
        }

        mContext = context;
        mResource = resource;
        this.objects = objects;
        refLike = FirebaseDatabase.getInstance().getReference("/recipes");
        refSave = FirebaseDatabase.getInstance().getReference("/userAccounts");
        refAuthor = FirebaseDatabase.getInstance().getReference("/userAccounts");
        refRoot = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if(context != null) {
            this.fm = ((FragmentActivity) context).getSupportFragmentManager();
        }
        this.fm = fm;

        Log.d(TAG, "Constructor called");

        if (!ImageLoader.getInstance().isInited()) {
            //sets up the image loader library
            setupImageLoader();
            Log.d(TAG, "Initialized");
        }
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //get the recipe
        final String name = getItem(position).getName();
        final String body = getItem(position).getCaption();
        final String imgUrl = getItem(position).getImageURL();
        final String posterId = getItem(position).getPostedBy().keySet().iterator().next();
        final List<Recipe.Ingredient> ingredients = getItem(position).getIngredients();
        final Recipe r = getItem(position);

        final String key = getItem(position).getDbKey(); // TODO: starting point for debug


        Log.d(TAG, "getView called");

        try {
            //ViewHolder object
            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder = new ViewHolder();
                holder.setRecipeId(key);
                holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
                holder.image = (ImageView) convertView.findViewById(R.id.cardImage);
                holder.dialog = (ProgressBar) convertView.findViewById(R.id.cardProgressDialog);
                holder.subtitle = convertView.findViewById(R.id.cardSubtitle);
                holder.body = convertView.findViewById(R.id.cardBody);
                holder.bullets = convertView.findViewById(R.id.cardBullets);
                holder.setLikeButton(key);
                holder.setSaveButton(key);
                holder.likeButton = (ImageButton) convertView.findViewById(R.id.likeButton);
                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;
                        refLike.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    if (dataSnapshot.child(key).hasChild("likedBy")) {
                                        if (dataSnapshot.child(key).child("likedBy").hasChild(userId)) {
                                            //Already liked
                                            refLike.child(key).child("likedBy").child(userId).removeValue();
                                            refSave.child(userId).child("likedRecipes").child(key).removeValue();
                                            log("UNLIKE", "User: " + userId + ", " + "Recipe: " + key);
                                            mProcessLike = false;
                                        } else {
                                            //Not liked yet
                                            refLike.child(key).child("likedBy").child(userId).setValue(true);
                                            refSave.child(userId).child("likedRecipes").child(key).setValue(true);
                                            log("LIKE", "User: " + userId + ", " + "Recipe: " + key);
                                            sendLikeNotif(name, key, imgUrl, posterId);
                                            mProcessLike = false;
                                        }
                                    } else {
                                        // doesn't have likedBy yet
                                        refLike.child(key).child("likedBy").child(userId).setValue(true);
                                        refSave.child(userId).child("likedRecipes").child(key).setValue(true);
                                        log("LIKE", "User: " + userId + ", " + "Recipe: " + key);

                                        sendLikeNotif(name, key, imgUrl, posterId);
                                        mProcessLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                //holder.description = (TextView) convertView.findViewById(R.id.cardDescription);

                holder.saveForLaterButton = (ImageButton) convertView.findViewById(R.id.saveButton);
                holder.saveForLaterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessSave = true;
                        refSave.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessSave) {
                                    String key = holder.getRecipeId();
                                    String userId = mAuth.getCurrentUser().getUid();
                                    if (dataSnapshot.child(userId).hasChild("savedForLater")) {
                                        if (dataSnapshot.child(userId).child("savedForLater").hasChild(key)) {
                                            //Already liked
                                            refSave.child(userId).child("savedForLater").child(key).removeValue();
                                            log("UNSAVE", "User: " + userId + ", " + "Recipe: " + key);

                                            mProcessSave = false;
                                        } else {
                                            //Not liked yet
                                            refSave.child(userId).child("savedForLater").child(key).setValue(true);
                                            log("SAVE", "User: " + userId + ", " + "Recipe: " + key);

                                            mProcessSave = false;
                                        }
                                    } else {
                                        // doesn't have likedBy yet
                                        refSave.child(userId).child("savedForLater").child(key).setValue(true);
                                        log("SAVE", "User: " + userId + ", " + "Recipe: " + key);

                                        mProcessSave = false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            lastPosition = position;

            holder.title.setText(name);
            holder.body.setText(body);

            mProcessCardBullets = true;

            refRoot.child("pantries").child(AuthUserInfo.INSTANCE.getUser().getPersonalPantry()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Pantry pantry = dataSnapshot.getValue(Pantry.class);
                    refRoot.child("items").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int numMissing = 0;
                            for(Recipe.Ingredient ingredient : ingredients) {
                                boolean weHaveIt = false;
                                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                    if(pantry.getItems().containsKey(itemSnapshot.getKey())) {      // if we have this item
                                        Item item = itemSnapshot.getValue(Item.class);
                                        String ingName = ingredient.getName().toLowerCase();
                                        String itemName = item.getName().toLowerCase();
                                        if (!(ingName.equals("") || itemName.equals("")) && ingName.contains(itemName) || itemName.contains(ingName)) {
                                            weHaveIt = true;
                                            break;
                                        }
                                    }
                                }
                                if(!weHaveIt) {
                                    numMissing++;
                                }
                                //Log.d("This is numMissing for " + getItem(position).getName(), numMissing+"");
                            }
                            if(mProcessCardBullets) {
                                LinearLayout bulletLayout = new LinearLayout(getContext());
                                TextView tv = new TextView(getContext());
                                ImageView bullet = new ImageView(getContext());

                                if(numMissing == 0) {
                                    tv.setText("You have all the ingredients to make this!");
                                    bullet.setImageResource(R.drawable.green);

                                } else {
                                    String ingStr = numMissing == 1 ? "ingredient" : "ingredients";
                                    tv.setText(String.format(Locale.US, "You are missing %d %s.", numMissing, ingStr));
                                    bullet.setImageResource(R.drawable.red);
                                }
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
                                layoutParams.setMargins(0, 15, 15, 0);
                                bullet.setLayoutParams(layoutParams);
                                bulletLayout.addView(bullet);
                                bulletLayout.addView(tv);
                                holder.bullets.removeAllViews();
                                holder.bullets.addView(bulletLayout);
                                mProcessCardBullets = false;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            refAuthor.child(posterId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User posterUser = dataSnapshot.getValue(User.class);
                    holder.subtitle.setText(posterUser.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed", null, mContext.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, holder.image, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.dialog.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.dialog.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.dialog.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    }

            );
            Log.d(TAG, "returnimg convertView");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction t = fm.beginTransaction();
                    t.replace(R.id.frame_layout, RecipeFragment.newFragment(getItem(position).getDbKey()));
                    t.addToBackStack(null).commit();
                }
            });
            return convertView;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage());
            return convertView;
        }

    }

    private void sendLikeNotif(String recipeName, String recipeId, String recipeImage, final String destId) {
        mAddLikeNotifToUser = true;
        Notification notif = new Notification();
        final String notifId = UUID.randomUUID().toString().substring(0, 30).replace("-", "");
        notif.setMessage(AuthUserInfo.INSTANCE.getUser().getName() + " has liked this recipe.");
        notif.setOriginator(recipeName);
        notif.setLinkID(recipeId);
        notif.setLinkType("recipe");
        notif.setImageURL(recipeImage);
        notif.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("userAccounts").child(destId).child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mAddLikeNotifToUser) {
                    Map<String, Boolean> map = (Map<String, Boolean>)dataSnapshot.getValue();
                    if(map == null) {
                        map = Maps.newHashMap();
                    }
                    map.put(notifId, true);
                    ref.child("userAccounts").child(destId).child("notifications").setValue(map);
                    mAddLikeNotifToUser = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("notifications").child(notifId).setValue(notif);
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    /**
     * Holds variables in a View
     */
    private class ViewHolder {
        TextView title;
        TextView subtitle;
        TextView body;
        LinearLayout bullets;
        //        TextView description;
        ImageView image;
        ProgressBar dialog;
        ImageButton likeButton;
        ImageButton saveForLaterButton;
        DatabaseReference mDatabaseLike = FirebaseDatabase.getInstance().getReference("/recipes");
        DatabaseReference mDatabaseSave = FirebaseDatabase.getInstance().getReference("/userAccounts");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        String recipeId;

        private ViewHolder() {
            mDatabaseLike.keepSynced(true);

        }


        public String getRecipeId() {
            return recipeId;
        }

        public void setRecipeId(String recipeId) {
            this.recipeId = recipeId;
        }


        private void setLikeButton(final String dbKey) {
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userId = mAuth.getCurrentUser().getUid();
                    if (dataSnapshot.child(dbKey).hasChild("likedBy")) {
                        if (dataSnapshot.child(dbKey).child("likedBy").hasChild(userId)) {
                            //Already liked
                            likeButton.setImageResource(R.drawable.like_pink);
                        } else {
                            //Not liked yet
                            likeButton.setImageResource(R.drawable.like_gray);
                        }
                    } else {
                        // doesn't have likedBy yet
                        likeButton.setImageResource(R.drawable.like_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void setSaveButton(final String dbKey) {
            mDatabaseSave.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userId = mAuth.getCurrentUser().getUid();
                    if (dataSnapshot.child(userId).hasChild("savedForLater")) {
                        if (dataSnapshot.child(userId).child("savedForLater").hasChild(dbKey)) {
                            //Already liked
                            saveForLaterButton.setImageResource(R.drawable.saved);
                        } else {
                            //Not liked yet
                            saveForLaterButton.setImageResource(R.drawable.save);
                        }
                    } else {
                        // doesn't have likedBy yet
                        saveForLaterButton.setImageResource(R.drawable.save);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void log(String eventType, String value) {
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(eventType, value);
            mFirebaseAnalytics.logEvent("HomeFragment", bundle);
        }
    }
}