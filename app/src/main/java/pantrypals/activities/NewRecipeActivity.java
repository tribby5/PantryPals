package pantrypals.activities;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.databaes.pantrypals.R;
import com.google.common.collect.Table;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pantrypals.models.Recipe;

public class NewRecipeActivity extends AppCompatActivity {

    private String mRecipe_key = null;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        mDatabase = FirebaseDatabase.getInstance().getReference("/TESTrecipes");
        mRecipe_key = mDatabase.push().getKey();

        // Setonclicklistener on addrow button

        Button createNewRecipeButton = (Button) findViewById(R.id.createRecipeButton);
        createNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleView = (EditText) findViewById(R.id.newRecipeTitle);
                EditText captionView = (EditText) findViewById(R.id.newRecipeCaption);
                EditText textView = (EditText) findViewById(R.id.newRecipeText);
                String title = titleView.getText().toString();
                String caption = captionView.getText().toString();
                String text = textView.getText().toString();

                List<Recipe.Ingredient> ingredients = new ArrayList<>();
                TableLayout ingredientTableView = (TableLayout) findViewById(R.id.newRecipeIngredientTable);
                for (int i = 0, j = ingredientTableView.getChildCount(); i < j; i++) {
                    View tableRow = ingredientTableView.getChildAt(i);
                    if (tableRow instanceof TableRow) {
                        TableRow row = (TableRow) tableRow;
                        String name = null;
                        Double amount = 0.0;
                        String unit = null;
                        for (int x = 0; x < row.getChildCount(); x++) {
                            View ingredientElem = row.getChildAt(x);
                            if (ingredientElem instanceof EditText) {
                                EditText data = (EditText) ingredientElem;
                                if (x == 0) {
                                    name = data.getText().toString();
                                } else if (x == 1) {
                                    amount = Double.parseDouble(data.getText().toString());
                                } else if (x == 2) {
                                    unit = data.getText().toString();
                                }
                            }
                        }
                        Recipe.Ingredient ingredientInstance = new Recipe.Ingredient();
                        ingredientInstance.setName(name);
                        ingredientInstance.setAmount(amount);
                        ingredientInstance.setUnit(unit);
                        ingredients.add(ingredientInstance);
                    }
                }
            }
        });

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
