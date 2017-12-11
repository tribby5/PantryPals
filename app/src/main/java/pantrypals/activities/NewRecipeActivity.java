package pantrypals.activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

    private EditText prevIngNameField;
    private EditText prevIngAmtField;
    private EditText prevIngUnitField;

    private DatabaseReference mDatabase;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //TODO: When this is finished, change the string inside getReference to /recipes
        mDatabase = FirebaseDatabase.getInstance().getReference("/TESTRECIPES");
        final String mRecipe_key = mDatabase.push().getKey();

        prevIngNameField = (EditText) findViewById(R.id.ingredientName);
        prevIngAmtField = (EditText) findViewById(R.id.ingredientAmount);
        prevIngUnitField = (EditText) findViewById(R.id.ingredientUnit);

        final TableLayout ingredientTableView = (TableLayout) findViewById(R.id.newRecipeIngredientTable);

        // Setonclicklistener on addRow button
        Button addIngredientButton = (Button) findViewById(R.id.addIngredientButton);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prevFieldsFilled()) {
                    prevIngNameField.clearFocus();
                    prevIngNameField.getBackground().clearColorFilter();
                    prevIngAmtField.clearFocus();
                    prevIngAmtField.getBackground().clearColorFilter();
                    prevIngUnitField.clearFocus();
                    prevIngUnitField.getBackground().clearColorFilter();

                    TableRow newRow = new TableRow(NewRecipeActivity.this);
                    EditText ingNameField = new EditText(NewRecipeActivity.this);
                    EditText ingAmtField = new EditText(NewRecipeActivity.this);
                    EditText ingUnitField = new EditText(NewRecipeActivity.this);

                    ingNameField.setWidth(150);
                    ingAmtField.setWidth(50);
                    ingUnitField.setWidth(50);

                    ingNameField.setTextColor(getResources().getColor(R.color.colorWhite));
                    ingNameField.setHintTextColor(getResources().getColor(R.color.colorHint));
                    ingAmtField.setTextColor(getResources().getColor(R.color.colorWhite));
                    ingAmtField.setHintTextColor(getResources().getColor(R.color.colorHint));
                    ingUnitField.setTextColor(getResources().getColor(R.color.colorWhite));
                    ingUnitField.setHintTextColor(getResources().getColor(R.color.colorHint));

                    ingNameField.setHint("ingredient");
                    ingAmtField.setHint("amt");
                    ingUnitField.setHint("unit");

                    ingNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    ingAmtField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    ingUnitField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

                    ingNameField.setGravity(Gravity.CENTER);
                    ingAmtField.setGravity(Gravity.CENTER);
                    ingUnitField.setGravity(Gravity.CENTER);

                    prevIngNameField = ingNameField;
                    prevIngAmtField = ingAmtField;
                    prevIngUnitField = ingUnitField;

                    ingNameField.requestFocus();

                    newRow.addView(ingNameField);
                    newRow.addView(ingAmtField);
                    newRow.addView(ingUnitField);
                    ingredientTableView.addView(newRow);
                }
            }
        });

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
                for (int i = 0, j = ingredientTableView.getChildCount(); i < j; i++) {
                    View tableRow = ingredientTableView.getChildAt(i);
                    if (tableRow instanceof TableRow) {
                        TableRow row = (TableRow) tableRow;
                        String ingName = null;
                        double amount = 0.0;
                        String unit = null;
                        for (int x = 0; x < row.getChildCount(); x++) {
                            View ingredientElem = row.getChildAt(x);
                            if (ingredientElem instanceof EditText) {
                                EditText data = (EditText) ingredientElem;
                                if (x == 0) {
                                    ingName = data.getText().toString();
                                } else if (x == 1) {
                                    try {
                                        amount = (double) Integer.parseInt(data.getText().toString());
                                    } catch(Exception e) {
                                        try {
                                            amount = Double.parseDouble(data.getText().toString());
                                        } catch(Exception ee) {
                                            underlineRed(data);
                                        }
                                    }
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
                Date d = new Date();
                String timePosted = dateFormat.format(d); // Find todays date

                Recipe newRecipe = new Recipe();
                newRecipe.setName(name);
                newRecipe.setCaption(caption);
                newRecipe.setIngredients(ingredients);
                newRecipe.setTimePosted(timePosted);
                newRecipe.setNegTimestamp(d.getTime() * -1); // for sorting in Firebase
                //TODO: set instructions and image
                //newRecipe.setInstructions();

//                Map<String, Recipe> recipesToSendToFirebase = new HashMap<>();
//                recipesToSendToFirebase.put(mRecipe_key, newRecipe);

                mDatabase.child(mRecipe_key).setValue(newRecipe);

                toastMessage("New Recipe " + name + " has been successfully created");
                finish();
            }
        });

    }

    private boolean prevFieldsFilled() {
        boolean allFilled = true;
        if(prevIngNameField.getText().toString().isEmpty()) {
            underlineRed(prevIngNameField);
            allFilled = false;
        }
        if(prevIngAmtField.getText().toString().isEmpty()) {
            underlineRed(prevIngAmtField);
            allFilled = false;
        }
        if(prevIngUnitField.getText().toString().isEmpty()) {
            underlineRed(prevIngUnitField);
            allFilled = false;
        }
        return allFilled;
    }

    private void underlineRed(final EditText et) {
        et.getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_IN);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
