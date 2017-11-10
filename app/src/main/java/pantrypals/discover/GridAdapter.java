package pantrypals.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.List;

/**
 * Created by adityasrinivasan on 07/11/17.
 */

public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<String> items;

    GridAdapter(Context mContext, List<String> items) {
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
        String string = items.get(i);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.grid_item, null);

        SquareLayout sq = view.findViewById(R.id.grid_item_square);
        TextView tv = view.findViewById(R.id.grid_item_text);
        tv.setText(string);

        return sq;
    }
}