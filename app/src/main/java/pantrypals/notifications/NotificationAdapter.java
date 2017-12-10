package pantrypals.notifications;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;

import java.sql.Timestamp;
import java.util.List;

import pantrypals.discover.SquareLayout;
import pantrypals.models.Notification;
import pantrypals.profile.ProfileFragment;

/**
 * Created by adityasrinivasan on 09/12/17.
 */

public class NotificationAdapter extends BaseAdapter {

    private static final long MINUTES = 60 * 1000000000;

    private final Context mContext;
    private final List<Notification> items;
    private final FragmentManager fm;

    NotificationAdapter(Context mContext, List<Notification> items, FragmentManager fm) {
        this.mContext = mContext;
        this.items = items;
        this.fm = fm;
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
        final Notification notif = items.get(i);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.notification_item, null);

        RelativeLayout rl = view.findViewById(R.id.notification_layout);
        TextView tv1 = view.findViewById(R.id.notification_primary);
        tv1.setText(notif.getOriginator());
        TextView tv2 = view.findViewById(R.id.notification_secondary);
        tv2.setText(notif.getMessage());
        TextView tv3 = view.findViewById(R.id.notification_date);
        Timestamp timestamp = Timestamp.valueOf(notif.getTimestamp());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long secondsAgo = (now.getTime() - timestamp.getTime())/1000;
        String agoText;
        if (secondsAgo < 60) {
            agoText = secondsAgo + "s ago";
        } else if (secondsAgo < 60 * 60) {
            agoText = (secondsAgo/60) + "m ago";
        } else if (secondsAgo < 60 * 60 * 24) {
            agoText = (secondsAgo/3600) + "h ago";
        } else {
            agoText = (secondsAgo/(3600 * 24)) + "d ago";
        }
        tv3.setText(agoText);

        if(notif.getLinkType().equals("user")) {
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frame_layout, ProfileFragment.newFragment(notif.getLinkID()));
                    transaction.commit();
                }
            });
        }

        return rl;
    }
}
