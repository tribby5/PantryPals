package pantrypals.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.databaes.pantrypals.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewRecipeActivity extends AppCompatActivity {

    private String key = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
    }



//    try {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        return dateFormat.format(new Date()); // Find todays date
//    } catch (Exception e) {
//        e.printStackTrace();
//
//        return null;
//    }
}
