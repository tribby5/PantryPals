package pantrypals;

import android.app.Application;

import com.android.databaes.pantrypals.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by adityasrinivasan on 07/12/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
