package pantrypals.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by adityasrinivasan on 07/11/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments = Lists.newArrayList();
    List<String> tabNames = Lists.newArrayList();

    public void addTab(Fragment fragment, String tabName) {
        this.fragments.add(fragment);
        this.tabNames.add(tabName);
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }
}
