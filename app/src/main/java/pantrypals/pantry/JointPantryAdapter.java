package pantrypals.pantry;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pantrypals.models.JointPantry;

/**
 * Created by mtribby on 12/9/17.
 */

public class JointPantryAdapter extends BaseAdapter{

    private Activity activity;
    private List<JointPantry> jointPantries;

    public JointPantryAdapter(Activity activity, Collection<JointPantry> pantries){
        super();
        this.activity=activity;
        this.jointPantries= new ArrayList<>(pantries);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater=activity.getLayoutInflater();
        PantryHolder holder;

        if(view == null){
            view=inflater.inflate(android.R.layout.simple_list_item_1, null);
            holder = new PantryHolder();

            holder.title = view.findViewById(android.R.id.text1);
            holder.title.setTextColor(activity.getResources().getColor(R.color.colorWhite));

            view.setTag(holder);
        } else {
            holder = (PantryHolder) view.getTag();
        }

        TextView titleTV = holder.title;

        JointPantry result = (JointPantry) getItem(i);

        titleTV.setText(result.getTitle());

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public JointPantry getItem(int i) {
        return jointPantries.get(i);
    }

    @Override
    public int getCount() {
        return jointPantries.size();
    }

    public void refresh(Collection<JointPantry> pantries) {
        this.jointPantries = new ArrayList<>(pantries);
        notifyDataSetChanged();
    }

    public String getPantryIdFromDataSource(int position) {
        System.out.println("PRINT: adapter get id = "+jointPantries.get(position).getDatabaseId());
        return jointPantries.get(position).getDatabaseId();
    }

    private static class PantryHolder {
        TextView title;
    }

}
