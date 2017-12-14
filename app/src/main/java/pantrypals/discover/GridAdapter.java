package pantrypals.discover;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.stream.Collectors;

import pantrypals.discover.search.SearchResult;
import pantrypals.discover.search.SearchType;
import pantrypals.groups.GroupFragment;
import pantrypals.home.HomeFragment;
import pantrypals.models.Group;
import pantrypals.models.Recipe;
import pantrypals.models.User;
import pantrypals.profile.ProfileFragment;
import pantrypals.recipe.RecipeFragment;
import pantrypals.recipe.RecipeListFragment;
import pantrypals.util.DownloadImageTask;

/**
 * Created by adityasrinivasan on 07/11/17.
 */

public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<DiscoverDetailFragment.DiscoverResult> items;
    private FragmentManager fm;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private ValueEventListener mCommunitiesListener;

    GridAdapter(Context mContext, List<DiscoverDetailFragment.DiscoverResult> items) {
        this.mContext = mContext;
        this.items = items;
        if(mContext != null) {
            this.fm = ((FragmentActivity) mContext).getSupportFragmentManager();
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DiscoverDetailFragment.DiscoverResult result = items.get(i);
        final String string = result.title;
        final String type = result.type;

        final StringBuilder resultType = new StringBuilder();
        final StringBuilder resultID = new StringBuilder();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.grid_item, null);

        SquareLayout sq = view.findViewById(R.id.grid_item_square);
        final ImageView iv = view.findViewById(R.id.grid_item_image);
        final TextView tv = view.findViewById(R.id.grid_item_text);

        if(!type.equals("trending")) {
            String iconName = string.split(" ")[0].toLowerCase().replace("-", "_");
            int resID = mContext.getResources().getIdentifier(iconName, "drawable", mContext.getPackageName());
            iv.setImageResource(resID);

            if(string.contains("Breakfast") || string.contains("Holidays")) {
                tv.setText(string.split(" ")[0]);
            } else {
                tv.setText(string);
            }
        } else {
            if(string.contains("@")) {
                String[] split = string.split("@");
                resultID.append(split[0]);
                resultType.append(split[1]);
                if (resultType.toString().equals("person")) {
                    mRef.child("userAccounts").child(resultID.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                new DownloadImageTask(iv).execute(user.getAvatar());
                                tv.setText(user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (resultType.toString().equals("recipe")) {
                    mRef.child("recipes").child(resultID.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Recipe recipe = dataSnapshot.getValue(Recipe.class);
                            if (recipe != null) {
                                new DownloadImageTask(iv).execute(recipe.getImageURL());
                                tv.setText(recipe.getName());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (resultType.toString().equals("group")) {
                    mRef.child("groups").child(resultID.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if (group != null) {
                                iv.setImageResource(R.drawable.group);
                                tv.setText(group.getName());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        sq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("moods") || type.equals("cuisines")) {
                    mRef.child("recipes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Recipe> recipes = Lists.newArrayList();
                            for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                                Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                                for(String tag : recipe.getTags()) {
                                    if(tag.toLowerCase().equals(string.toLowerCase())) {
                                        recipes.add(recipe);
                                        break;
                                    }
                                }
                            }
                            RecipeListFragment frag = RecipeListFragment.newFragment(string, recipes);
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.frame_layout, frag).addToBackStack(null).commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if(type.equals("trending")) {
                    Fragment frag;
                    if(resultType.toString().equals("person")) {
                        frag = ProfileFragment.newFragment(resultID.toString());
                    } else if(resultType.toString().equals("group")) {
                        frag = GroupFragment.newFragment(resultID.toString());
                    } else if(resultType.toString().equals("recipe")) {
                        frag = RecipeFragment.newFragment(resultID.toString());
                    } else {
                        frag = HomeFragment.newInstance();
                    }
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frame_layout, frag).addToBackStack(null).commit();
                } else if(type.equals("communities")) {
                    mCommunitiesListener = mRef.child("groups").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<SearchResult> groups = Lists.newArrayList();
                            for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                                Group group = groupSnapshot.getValue(Group.class);
                                Log.d("GridAdapter", group.getName() + " " + group.getCategory());
                                if(group.getCategory().toLowerCase().equals(string.toLowerCase())) {
                                    groups.add(new SearchResult(SearchType.GROUPS, group, groupSnapshot.getKey()));
                                }
                            }
                            DiscoverResultFragment frag = DiscoverResultFragment.newInstance(string, groups);
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.frame_layout, frag).addToBackStack(null).commit();
                            Log.d("GridAdapter", "HERE!!!");
                            mRef.child("groups").removeEventListener(mCommunitiesListener);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        return sq;
    }
}
