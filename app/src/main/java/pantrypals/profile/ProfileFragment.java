package pantrypals.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import pantrypals.discover.DiscoverDetailFragment;
import pantrypals.models.Notification;
import pantrypals.models.User;
import pantrypals.util.AuthUserInfo;
import pantrypals.util.DownloadImageTask;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private static final String ARG_ID = "ID";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private int prevSelected = -1;
    private int follow;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newFragment(CharSequence uid) {
        ProfileFragment fragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_ID, uid);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setProfileHeader(view);

        TabLayout tabLayout = view.findViewById(R.id.profile_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.profile_view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addTab(new ProfilePostsFragment(), "Posts");
        adapter.addTab(ProfileInfoFragment.newFragment(getArguments().getCharSequence(ARG_ID)), "Info");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setProfileHeader(final View view) {
        final TextView name = view.findViewById(R.id.profile_name);
        final TextView bio = view.findViewById(R.id.profile_bio);
        mDatabase.child("/userAccounts/" + getArguments().get(ARG_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                bio.setText(user.getBio());

                new DownloadImageTask((ImageView) view.findViewById(R.id.avatar))
                        .execute("https://vignette.wikia.nocookie.net/mrbean/images/4/4b/Mr_beans_holiday_ver2.jpg/revision/latest/scale-to-width-down/250?cb=20100424114324");

                ImageView verifiedImage = view.findViewById(R.id.verifiedIcon);
                verifiedImage.setVisibility(user.isVerified() ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Spinner spinner = view.findViewById(R.id.profile_follow_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, Lists.newArrayList("Not following", "Following all", "Following relevant"));
        spinner.setAdapter(adapter);

        mDatabase.child("/follows/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                follow = 0;
                for (DataSnapshot followingSnapshot : dataSnapshot.getChildren()) {
                    if (followingSnapshot.getKey().equals(getArguments().get(ARG_ID))) {
                        follow = (((String) followingSnapshot.getValue(String.class)).equals("all") ? 1 : 2);
                        break;
                    }
                }
                final String notifID = UUID.randomUUID().toString().substring(0, 32).replace("-", "");
                final Notification notif = new Notification();
                notif.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
                notif.setLinkType("user");
                notif.setLinkID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if(follow == 0) {
                    notif.setMessage("This user has unfollowed you.");
                } else if (follow == 1) {
                    notif.setMessage("This user has followed all your posts.");
                } else {
                    notif.setMessage("This user has followed your relevant posts.");
                }
                notif.setOriginator(AuthUserInfo.INSTANCE.getUser().getName());
                mDatabase.child("/userAccounts/" + getArguments().get(ARG_ID)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(prevSelected != -1 && follow != prevSelected) {
                            mDatabase.child("/notifications/" + notifID).setValue(notif);
                            User user = (User) dataSnapshot.getValue(User.class);
                            Map<String, Boolean> notifs = user.getNotifications();
                            if (notifs == null) {
                                notifs = Maps.newHashMap();
                            }
                            notifs.put(notifID, true);
                            user.setNotifications(notifs);
                            mDatabase.child("/userAccounts/" + getArguments().get(ARG_ID)).setValue(user);
                        }
                        prevSelected = follow;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                spinner.setSelection(follow);
                if(isAdded()) {
                    if (follow == 0) {
                        spinner.setBackgroundColor(getActivity().getResources().getColor(R.color.colorLightGray));
                    } else {
                        spinner.setBackgroundColor(getActivity().getResources().getColor(R.color.colorGreen));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                if(i == 0) {            // not following
                    mDatabase.child("/follows/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot followingSnapshot : dataSnapshot.getChildren()) {
                                if (spinner.getSelectedItemPosition() == 0 && followingSnapshot.getKey().equals(getArguments().get(ARG_ID))) {
                                    mDatabase.child("/follows/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + followingSnapshot.getKey()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (i == 1) {
                    mDatabase.child("/follows/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getArguments().get(ARG_ID).toString()).setValue("all");
                } else if (i == 2) {    // following relevant
                    mDatabase.child("/follows/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getArguments().get(ARG_ID).toString()).setValue("relevant");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(getArguments().get(ARG_ID))) {
            spinner.setVisibility(View.INVISIBLE);
        }

        final TextView numFollowing = view.findViewById(R.id.profile_following_num);
        mDatabase.child("/follows/" + getArguments().get(ARG_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num = 0;
                for (DataSnapshot ignored : dataSnapshot.getChildren()) {
                    num++;
                }
                numFollowing.setText(Integer.toString(num));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final TextView numFollowers = view.findViewById(R.id.profile_followers_num);
        mDatabase.child("/follows").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot followingSnapshot : userSnapshot.getChildren()) {
                        if (followingSnapshot.getKey().equals(getArguments().get(ARG_ID))) {
                            num++;
                        }
                    }
                }
                numFollowers.setText(Integer.toString(num));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
