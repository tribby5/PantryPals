package pantrypals.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databaes.pantrypals.R;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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

        TabLayout tabLayout = view.findViewById(R.id.profile_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.profile_view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addTab(new ProfilePostsFragment(), "Posts");
        adapter.addTab(new ProfilePantryFragment(), "Pantry");
        adapter.addTab(new ProfileInfoFragment(), "Info");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


}
