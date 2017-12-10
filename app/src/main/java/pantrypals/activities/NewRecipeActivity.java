package pantrypals.activities;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pantrypals.models.Recipe;
import pantrypals.models.User;

public class NewRecipeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //TODO: When this is finished, change the string inside getReference to /recipes
        mDatabase = FirebaseDatabase.getInstance().getReference("/TESTRECIPES");
        final String mRecipe_key = mDatabase.push().getKey();

        // Setonclicklistener on addRow button

        // SetOnClickListener for uploading image

        Button createNewRecipeButton = (Button) findViewById(R.id.createRecipeButton);
        createNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nameView = (EditText) findViewById(R.id.newRecipeTitle);
                EditText captionView = (EditText) findViewById(R.id.newRecipeCaption);
                String name = nameView.getText().toString();
                String caption = captionView.getText().toString();

                //TODO: Change the following two lines so that instead of text, we get list of instructions
                EditText textView = (EditText) findViewById(R.id.newRecipeText);
                String text = textView.getText().toString();

                List<Recipe.Ingredient> ingredients = new ArrayList<>();
                TableLayout ingredientTableView = (TableLayout) findViewById(R.id.newRecipeIngredientTable);
                for (int i = 0, j = ingredientTableView.getChildCount(); i < j; i++) {
                    View tableRow = ingredientTableView.getChildAt(i);
                    if (tableRow instanceof TableRow) {
                        TableRow row = (TableRow) tableRow;
                        String ingName = null;
                        Double amount = 0.0;
                        String unit = null;
                        for (int x = 0; x < row.getChildCount(); x++) {
                            View ingredientElem = row.getChildAt(x);
                            if (ingredientElem instanceof EditText) {
                                EditText data = (EditText) ingredientElem;
                                if (x == 0) {
                                    ingName = data.getText().toString();
                                } else if (x == 1) {
                                    amount = Double.parseDouble(data.getText().toString());
                                } else if (x == 2) {
                                    unit = data.getText().toString();
                                }
                            }
                        }
                        Recipe.Ingredient ingredientInstance = new Recipe.Ingredient();
                        ingredientInstance.setName(ingName);
                        ingredientInstance.setAmount(amount);
                        ingredientInstance.setUnit(unit);
                        ingredients.add(ingredientInstance);
                    }
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String timePosted = dateFormat.format(new Date()); // Find todays date

                Recipe newRecipe = new Recipe();
                newRecipe.setName(name);
                newRecipe.setCaption(caption);
                newRecipe.setIngredients(ingredients);
                newRecipe.setTimePosted(timePosted);
                //TODO: set Instructions
                //newRecipe.setInstructions();

//                Map<String, Recipe> recipesToSendToFirebase = new HashMap<>();
//                recipesToSendToFirebase.put(mRecipe_key, newRecipe);

                mDatabase.child(mRecipe_key).setValue(newRecipe);

                toastMessage("New Recipe " + name + " has been successfully created");
                finish();
            }
        });

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}