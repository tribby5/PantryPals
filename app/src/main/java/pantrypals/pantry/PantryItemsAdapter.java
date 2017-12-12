package pantrypals.pantry;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pantrypals.models.Item;

/**
 * Created by mtribby on 11/9/17.
 */
//Made with help from this tutorial: http://techlovejump.com/android-multicolumn-listview/
public class PantryItemsAdapter extends BaseAdapter{

    private Activity activity;
    private List<Item> items;


    public PantryItemsAdapter(Activity activity, Collection<Item> items){
        super();
        this.activity=activity;
        this.items = new ArrayList<>(items);
    }

    void refresh(ArrayList<Item> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    String getItemNameFromDataSource(int position) {
        return items.get(position).getName();
    }

    @Override
    public View getView(int position, View rowView, ViewGroup viewGroup) {
        LayoutInflater inflater=activity.getLayoutInflater();
        PantryItemHolder holder;

        if(rowView == null){
            rowView=inflater.inflate(R.layout.item_row, null);
            holder = new PantryItemHolder();

            holder.name =(TextView) rowView.findViewById(R.id.item_name);
            holder.amount =(TextView) rowView.findViewById(R.id.item_amount);
            holder.unit =(TextView) rowView.findViewById(R.id.item_unit);
            holder.expirationDate=(TextView) rowView.findViewById(R.id.item_expiration);

            rowView.setTag(holder);
        } else {
            holder = (PantryItemHolder) rowView.getTag();
        }

        TextView nameTV = holder.name;
        TextView amountTV = holder.amount;
        TextView unitTV = holder.unit;
        TextView expirationDate = holder.expirationDate;

        Item result = (Item) getItem(position);

        nameTV.setText(result.getName());
        nameTV.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        amountTV.setText(Double.toString(result.getAmount()));
        amountTV.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        unitTV.setText(result.getUnit());
        unitTV.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        expirationDate.setText(result.getExpiration());
        expirationDate.setTextColor(activity.getResources().getColor(R.color.colorWhite));


//        HashMap<String, String> map=items.get(i);
//        name.setText(map.get(PersonalPantryFragment.FIRST_COLUMN));
//        name.setTextColor(activity.getResources().getColor(R.color.colorWhite));
//        amount.setText(map.get(PersonalPantryFragment.SECOND_COLUMN));
//        amount.setTextColor(activity.getResources().getColor(R.color.colorWhite));
//        units.setText(map.get(PersonalPantryFragment.THIRD_COLUMN));
//        units.setTextColor(activity.getResources().getColor(R.color.colorWhite));
//        expirationDate.setText(map.get(PersonalPantryFragment.FOURTH_COLUMN));
//        expirationDate.setTextColor(activity.getResources().getColor(R.color.colorWhite));

        return rowView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class PantryItemHolder {
        TextView name;
        TextView amount;
        TextView unit;
        TextView expirationDate;
    }
}
