package pantrypals.pantry;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

/**
 * Created by mtribby on 12/11/17.
 */

public class GroceryItemAdapter extends BaseAdapter {
    public final static String GROCERY_TITLE = "TITLE";
    public final static String GROCERY_AMOUNT = "AMOUNT";
    public final static String GROCERY_UNIT = "UNIT";
    public final static String GROCERY_ITEM_ID = "ID";

    private List<Map<String, String>> data;
    private Activity activity;
    private CheckBox checkBox;
    private TextView title;
    private TextView amount;

    public GroceryItemAdapter(Activity activity, List<Map<String, String>> data){
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int index = i;
        LayoutInflater inflater=activity.getLayoutInflater();

        if(view == null){

            view=inflater.inflate(R.layout.grocery_list_item, null);

            checkBox = (CheckBox) view.findViewById(R.id.deleteItem);
            title = (TextView) view.findViewById(R.id.title);
            amount = (TextView) view.findViewById(R.id.amount);
        }

        Map<String, String> map = data.get(i);
        title.setText(map.get(GROCERY_TITLE));
        amount.setText(map.get(GROCERY_AMOUNT) + " " + map.get(GROCERY_UNIT));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                GroceryItemAdapter.this.removeItem(index);
            }
        });

        return view;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void removeItem(int i){
        Log.d("WORKS", Integer.toString(i));
        Log.d("WORKS", data.get(i).get(GROCERY_TITLE));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child(activity.getResources().getString(R.string.groceryData))
                .child(uid).child(data.get(i).get(GROCERY_ITEM_ID)).removeValue();

        data.remove(i);
        refresh(data);




    }

    public void refresh(List<Map<String, String>> data){
        this.data = data;
        notifyDataSetChanged();
    }


}
