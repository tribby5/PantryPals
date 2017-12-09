package pantrypals.discover.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.List;

import pantrypals.discover.SquareLayout;
import pantrypals.models.Group;
import pantrypals.models.Post;
import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 08/12/17.
 */

public class SearchResultAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<SearchResult> items;

    SearchResultAdapter(Context mContext, List<SearchResult> items) {
        this.mContext = mContext;
        this.items = items;
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
                view = layoutInflater.inflate(R.layout.search_result_person_item, null);
                RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);
                return rl;
            }
        } else if(result.getType() == SearchType.GROUPS) {
            return getGroupView(result, layoutInflater);
        } else if(result.getType() == SearchType.POSTS) {
            return getPostView(result, layoutInflater);
        }
        view = layoutInflater.inflate(R.layout.search_result_person_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);
        return rl;
    }

    private View getPersonView(SearchResult result, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.search_result_person_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);

        User user = (User) result.getInfo();

        TextView nameTV = view.findViewById(R.id.search_result_person_name);
        nameTV.setText(user.getName());
        TextView bioTV = view.findViewById(R.id.search_result_person_bio);
        bioTV.setText(user.getBio());
        ImageView verifiedImg = view.findViewById(R.id.search_result_person_verified);
        verifiedImg.setVisibility(user.isVerified() ? View.VISIBLE : View.INVISIBLE);

        return rl;
    }

    private View getGroupView(SearchResult result, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.search_result_person_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);
        return rl;
    }

    private View getPostView(SearchResult result, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.search_result_person_item, null);
        RelativeLayout rl = view.findViewById(R.id.search_result_person_layout);
        return rl;
    }


}
