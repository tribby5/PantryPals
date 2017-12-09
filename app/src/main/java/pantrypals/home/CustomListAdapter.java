package pantrypals.home;

import android.graphics.Bitmap;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
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

import java.util.ArrayList;

import pantrypals.models.TempRecipe;


public class CustomListAdapter extends ArrayAdapter<TempRecipe> {

    private static final String TAG = "CustomListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private boolean mProcessLike = false;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;


    /**
     * Default constructor for the PersonListAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public CustomListAdapter(Context context, int resource, ArrayList<TempRecipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        ref = FirebaseDatabase.getInstance().getReference("/recipes");
        mAuth = FirebaseAuth.getInstance();

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

        //get the persons information
        String name = getItem(position).getName();
        String imgUrl = getItem(position).getImgURL();
        final String key = getItem(position).getDbKey();


        Log.d(TAG, "getView called");

        try {
            //ViewHolder object
            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
                holder.image = (ImageView) convertView.findViewById(R.id.cardImage);
                holder.dialog = (ProgressBar) convertView.findViewById(R.id.cardProgressDialog);
                holder.setLikeButton(key);
                holder.likeButton = (ImageButton) convertView.findViewById(R.id.likeButton);
                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    if (dataSnapshot.child(key).hasChild("likedBy")) {
                                        if (dataSnapshot.child(key).child("likedBy").hasChild(userId)) {
                                            //Already liked
                                            ref.child(key).child("likedBy").child(userId).removeValue();
                                            mProcessLike = false;
                                        } else {
                                            //Not liked yet
                                            ref.child(key).child("likedBy").child(userId).setValue(true);
                                            mProcessLike = false;
                                        }
                                    } else {
                                        // doesn't have likedBy yet
                                        ref.child(key).child("likedBy").child(userId).setValue(true);
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

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            lastPosition = position;

            holder.title.setText(name);
            //holder.description.setText(description);


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
            return convertView;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage());
            return convertView;
        }

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
    private static class ViewHolder {
        TextView title;
        //        TextView description;
        ImageView image;
        ProgressBar dialog;
        ImageButton likeButton;
        DatabaseReference mDatabaseLike = FirebaseDatabase.getInstance().getReference("/recipes");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        private ViewHolder() {
            mDatabaseLike.keepSynced(true);

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
    }
}