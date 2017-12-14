package pantrypals.discover.search;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.List;

import pantrypals.groups.GroupFragment;
import pantrypals.models.Group;
import pantrypals.models.Post;
import pantrypals.models.User;
import pantrypals.profile.ProfileFragment;
import pantrypals.util.DownloadImageTask;

/**
 * Created by adityasrinivasan on 08/12/17.
 */

public class SearchResultAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<SearchResult> items;
    private FragmentManager fm;

    public SearchResultAdapter(Context mContext, List<SearchResult> items) {
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
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        SearchResult result = items.get(i);

        if(result.getType() == SearchType.PEOPLE) {
            return getPersonView(result, layoutInflater);
        } else if(result.getType() == SearchType.ALL) {
            if(result.getInfo() instanceof User) {
                return getPersonView(result, layoutInflater);
            } else if(result.getInfo() instanceof Group) {
                return getGroupView(result, layoutInflater);
            } else if(result.getInfo() instanceof Post) {
                return getPostView(result, layoutInflater);
            } else {
                view = layoutInflater.inflate(R.layout.search_result_item, null);
                RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);
                return rl;
            }
        } else if(result.getType() == SearchType.GROUPS) {
            return getGroupView(result, layoutInflater);
        } else if(result.getType() == SearchType.POSTS) {
            return getPostView(result, layoutInflater);
        }
        view = layoutInflater.inflate(R.layout.search_result_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);
        return rl;
    }

    private View getPersonView(final SearchResult result, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.search_result_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);

        User user = (User) result.getInfo();

        TextView categoryTV = view.findViewById(R.id.search_result_category);
        categoryTV.setText("PERSON");
        TextView nameTV = view.findViewById(R.id.search_result_primary);
        nameTV.setText(user.getName());
        TextView bioTV = view.findViewById(R.id.search_result_secondary);
        bioTV.setText(user.getBio());
        ImageView verifiedImg = view.findViewById(R.id.search_result_verified);
        verifiedImg.setVisibility(user.isVerified() ? View.VISIBLE : View.INVISIBLE);
        ImageView iv = view.findViewById(R.id.search_result_person_avatar);
        new DownloadImageTask(iv).execute(user.getAvatar());

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fm != null) {
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frame_layout, ProfileFragment.newFragment(result.getId()));
                    transaction.addToBackStack(null).commit();
                }
            }
        });

        return rl;
    }

    private View getGroupView(final SearchResult result, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.search_result_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);

        Group group = (Group) result.getInfo();

        TextView categoryTV = view.findViewById(R.id.search_result_category);
        categoryTV.setText("GROUP");
        TextView nameTV = view.findViewById(R.id.search_result_primary);
        nameTV.setText(group.getName());
        TextView bioTV = view.findViewById(R.id.search_result_secondary);
        bioTV.setText(Integer.toString(group.getMembers().size()) + " members");

        ImageView verifiedImg = view.findViewById(R.id.search_result_verified);
        verifiedImg.setVisibility(View.INVISIBLE);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fm != null) {
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frame_layout, GroupFragment.newFragment(result.getId()));
                    transaction.addToBackStack(null).commit();
                }
            }
        });

        return rl;
    }

    private View getPostView(SearchResult result, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.search_result_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);

        Post post = (Post) result.getInfo();

        TextView categoryTV = view.findViewById(R.id.search_result_category);
        categoryTV.setText("POST");
        TextView nameTV = view.findViewById(R.id.search_result_primary);
        nameTV.setText(post.getTitle());
        TextView bioTV = view.findViewById(R.id.search_result_secondary);
        bioTV.setText(post.getText());

        ImageView verifiedImg = view.findViewById(R.id.search_result_verified);
        verifiedImg.setVisibility(View.INVISIBLE);

        return rl;
    }


}
