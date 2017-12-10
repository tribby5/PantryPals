package pantrypals.pantry;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mtribby on 11/9/17.
 */
//Made with help from this tutorial: http://techlovejump.com/android-multicolumn-listview/
public class PantryItemsAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<HashMap<String, String>> items;
    private TextView name;
    private TextView amount;
    private TextView units;
    private TextView expirationDate;


    public PantryItemsAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.items=list;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater=activity.getLayoutInflater();

        if(view == null){

            view=inflater.inflate(R.layout.item_row, null);

            name =(TextView) view.findViewById(R.id.name);
            amount =(TextView) view.findViewById(R.id.amount);
            units =(TextView) view.findViewById(R.id.unit);
            expirationDate=(TextView) view.findViewById(R.id.expiration);

        }

        HashMap<String, String> map=items.get(i);
        name.setText(map.get(PersonalPantryFragment.FIRST_COLUMN));
        name.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        amount.setText(map.get(PersonalPantryFragment.SECOND_COLUMN));
        amount.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        units.setText(map.get(PersonalPantryFragment.THIRD_COLUMN));
        units.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        expirationDate.setText(map.get(PersonalPantryFragment.FOURTH_COLUMN));
        expirationDate.setTextColor(activity.getResources().getColor(R.color.colorWhite));

        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public HashMap<String, String> getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
