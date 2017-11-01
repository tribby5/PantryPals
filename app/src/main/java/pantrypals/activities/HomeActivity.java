package pantrypals.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.databaes.pantrypals.R;

import pantrypals.discover.DiscoverFragment;
import pantrypals.home.HomeFragment;
import pantrypals.notifications.NotificationsFragment;
import pantrypals.pantry.PantryFragment;
import pantrypals.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

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

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();
    }




}
