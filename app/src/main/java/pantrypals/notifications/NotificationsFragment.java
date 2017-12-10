package pantrypals.notifications;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Sets;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

import pantrypals.models.Notification;


public class NotificationsFragment extends Fragment {

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the recipe_layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        final ListView lv = view.findViewById(R.id.notifications_list_view);
        final TextView noInfoText = view.findViewById(R.id.no_notifs_text);

        mDatabase.child("/userAccounts/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Set<String> notifIDs = Sets.newHashSet();
                for (DataSnapshot notifSnapshot : dataSnapshot.getChildren()) {
                    notifIDs.add(notifSnapshot.getKey());
                }
                mDatabase.child("/notifications").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Notification> notifs = Lists.newArrayList();
                        for(DataSnapshot notifSnapshot : dataSnapshot.getChildren()) {
                            if(notifIDs.contains(notifSnapshot.getKey())) {
                                Notification notif = notifSnapshot.getValue(Notification.class);
                                notifs.add(notif);
                            }
                        }
                        NotificationAdapter adapter = new NotificationAdapter(getActivity(), notifs, getActivity().getSupportFragmentManager());
                        lv.setAdapter(adapter);

                        if(notifs.size() == 0) {
                            lv.setVisibility(View.GONE);
                            noInfoText.setVisibility(View.VISIBLE);
                        } else {
                            lv.setVisibility(View.VISIBLE);
                            noInfoText.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
