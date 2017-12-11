package pantrypals.pantry;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pantrypals.models.Item;

/**
 * Created by mtribby on 12/9/17.
 */

public class JointPantryAdapter extends BaseAdapter{
    public static final String KEY = "key";
    public static final String TITLE = "title";
    private TextView title;

    private Activity activity;
    private ArrayList<HashMap<String, String>> jointPantries;

    public JointPantryAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.jointPantries=list;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater=activity.getLayoutInflater();

        if(view == null){
            view=inflater.inflate(android.R.layout.simple_list_item_1, null);
            title = view.findViewById(android.R.id.text1);
            title.setText(jointPantries.get(i).get(TITLE));
            title.setTextColor(activity.getResources().getColor(R.color.colorWhite));

        }
        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return jointPantries.get(i);
    }

    @Override
    public int getCount() {
        return jointPantries.size();
    }

}
