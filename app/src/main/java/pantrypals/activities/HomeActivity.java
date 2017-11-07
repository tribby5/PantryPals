package pantrypals.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.databaes.pantrypals.R;

import pantrypals.discover.DiscoverDetailFragment;
import pantrypals.discover.DiscoverFragment;
import pantrypals.discover.DiscoverItemClickListener;
import pantrypals.home.HomeFragment;
import pantrypals.notifications.NotificationsFragment;
import pantrypals.pantry.PantryFragment;
import pantrypals.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity implements DiscoverItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                selectedFragment = HomeFragment.newInstance();
                                break;
                            case R.id.nav_discover:
                                selectedFragment = DiscoverFragment.newInstance();
                                break;
                            case R.id.nav_pantry:
                                selectedFragment = PantryFragment.newInstance();
                                break;
                            case R.id.nav_profile:
                                selectedFragment = ProfileFragment.newInstance();
                                break;
                            case R.id.nav_notifications:
                                selectedFragment = NotificationsFragment.newInstance();
                                break;
                        }

                        setFragment(selectedFragment);
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
       setFragment(HomeFragment.newInstance());
    }

    @Override
    public void discoverItemClicked(CharSequence title) {
        setFragment(DiscoverDetailFragment.newFragment(title));
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commit();
    }
}
