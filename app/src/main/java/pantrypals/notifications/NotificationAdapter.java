package pantrypals.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.util.List;

import pantrypals.discover.SquareLayout;
import pantrypals.models.Notification;

/**
 * Created by adityasrinivasan on 09/12/17.
 */

public class NotificationAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Notification> items;

    NotificationAdapter(Context mContext, List<Notification> items) {
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
        Notification notif = items.get(i);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.notification_item, null);

        RelativeLayout rl = view.findViewById(R.id.notification_layout);
        TextView tv1 = view.findViewById(R.id.notification_primary);
        tv1.setText(notif.getOriginator());
        TextView tv2 = view.findViewById(R.id.notification_secondary);
        tv2.setText(notif.getMessage());
        TextView tv3 = view.findViewById(R.id.notification_date);
        tv3.setText(notif.getTimestamp());

        return rl;
    }
}
