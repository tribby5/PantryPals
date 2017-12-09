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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.databaes.pantrypals.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;

import pantrypals.discover.DiscoverDetailFragment;
import pantrypals.models.User;
import pantrypals.util.DownloadImageTask;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private static final String ARG_ID = "ID";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
                        .execute(user.getAvatar());

                ImageView verifiedImage = view.findViewById(R.id.verifiedIcon);
                verifiedImage.setVisibility(user.isVerified() ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final TextView numFollowing = view.findViewById(R.id.profile_following_num);
        mDatabase.child("/follows/" + getArguments().get(ARG_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num = 0;
                for(DataSnapshot ignored : dataSnapshot.getChildren()) {
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
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot followingSnapshot : userSnapshot.getChildren()) {
                        if(followingSnapshot.getKey().equals(getArguments().get(ARG_ID))) {
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
