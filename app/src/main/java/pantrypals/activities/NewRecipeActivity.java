package pantrypals.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    private static final int GALLERY_INTENT = 2;
    private String uploadedImagePath = null;

    public static final String CONTEXT_KEY = "ctx";

    private EditText prevIngNameField;
    private EditText prevIngAmtField;
    private EditText prevIngUnitField;
    private EditText prevInstr;

    private int stepNumber = 1;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    private Button uploadImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //TODO: When this is finished, change the string inside getReference to /recipes
        mDatabase = FirebaseDatabase.getInstance().getReference("/recipes");
        final String mRecipe_key = mDatabase.push().getKey();

        prevIngNameField = (EditText) findViewById(R.id.ingredientName);
        prevIngAmtField = (EditText) findViewById(R.id.ingredientAmount);
        prevIngUnitField = (EditText) findViewById(R.id.ingredientUnit);
        prevInstr = (EditText) findViewById(R.id.instructionText);

        final TableLayout ingredientTableView = (TableLayout) findViewById(R.id.newRecipeIngredientTable);
        final TableLayout instructionTableView = (TableLayout) findViewById(R.id.newRecipeInstructionTable);

        final String ctx = getIntent().getStringExtra(CONTEXT_KEY);

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
                    ingNameField.setHintTextColor(getResources().getColor(R.color.colorHintDark));
                    ingAmtField.setTextColor(getResources().getColor(R.color.colorWhite));
                    ingAmtField.setHintTextColor(getResources().getColor(R.color.colorHintDark));
                    ingUnitField.setTextColor(getResources().getColor(R.color.colorWhite));
                    ingUnitField.setHintTextColor(getResources().getColor(R.color.colorHintDark));

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

        Button addInstructionButton = (Button) findViewById(R.id.addInstructionButton);
        addInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prevInstr.getText().toString().isEmpty()) {
                    underlineRed(prevInstr);
                } else {
                    stepNumber++;
                    prevInstr.clearFocus();
                    prevInstr.getBackground().clearColorFilter();

                    TextView numTV = new TextView(NewRecipeActivity.this);
                    numTV.setTextAppearance(NewRecipeActivity.this, R.style.AppTheme_TextAppearance_Bold);
                    numTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    numTV.setTextColor(getResources().getColor(R.color.colorWhite));
                    numTV.setText(stepNumber + ". ");

                    TableRow newRow = new TableRow(NewRecipeActivity.this);
                    EditText instrField = new EditText(NewRecipeActivity.this);

                    instrField.setTextColor(getResources().getColor(R.color.colorWhite));
                    instrField.setHintTextColor(getResources().getColor(R.color.colorHintDark));

                    instrField.setHint("instruction");

                    instrField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

                    prevInstr = instrField;

                    instrField.requestFocus();

                    newRow.addView(numTV);
                    newRow.addView(instrField);
                    instructionTableView.addView(newRow);
                }
            }
        });

        // SetOnClickListener for uploading image
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        final EditText tagsET = (EditText) findViewById(R.id.create_recipe_tags);

        Button createNewRecipeButton = (Button) findViewById(R.id.createRecipeButton);
        createNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nameView = (EditText) findViewById(R.id.newRecipeTitle);
                EditText captionView = (EditText) findViewById(R.id.newRecipeCaption);
                String name = nameView.getText().toString();
                String caption = captionView.getText().toString();

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

                List<String> instructions = Lists.newArrayList();
                for (int i = 0, j = instructionTableView.getChildCount(); i < j; i++) {
                    View tableRow = instructionTableView.getChildAt(i);
                    if (tableRow instanceof TableRow) {
                        TableRow row = (TableRow) tableRow;
                        for (int x = 0; x < row.getChildCount(); x++) {
                            View elem = row.getChildAt(x);
                            if (elem instanceof EditText) {
                                EditText data = (EditText) elem;
                                instructions.add(data.getText().toString());
                            }
                        }
                    }
                }

                List<String> tags = Lists.newArrayList();
                for(String tag : tagsET.getText().toString().split(",")) {
                    tags.add(tag.trim());
                }

                Recipe newRecipe = new Recipe();

                // set postedBy
                mAuth = FirebaseAuth.getInstance();
                Map<String, Boolean> postedBy = new HashMap<>();
                postedBy.put(mAuth.getCurrentUser().getUid(), true);
                newRecipe.setPostedBy(postedBy);

                newRecipe.setName(name);
                newRecipe.setCaption(caption);
                newRecipe.setIngredients(ingredients);
                newRecipe.setTimePosted(timePosted);
                newRecipe.setNegTimestamp(d.getTime() * -1); // for sorting in Firebase
                newRecipe.setImageURL(uploadedImagePath);
                //newRecipe.setInstructions();
                newRecipe.setInstructions(instructions);
                newRecipe.setTags(tags);
                if(ctx != null) {
                    newRecipe.setGroupId(ctx);
                }

//                Map<String, Recipe> recipesToSendToFirebase = new HashMap<>();
//                recipesToSendToFirebase.put(mRecipe_key, newRecipe);

                mDatabase.child(mRecipe_key).setValue(newRecipe);

                toastMessage("New Recipe " + name + " has been successfully created.");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgressDialog.setMessage("Uploading image...");
            mProgressDialog.show();
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("RecipeImages").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadedImagePath = taskSnapshot.getDownloadUrl().toString();
                    toastMessage("Image upload succeeded.");
                    mProgressDialog.dismiss();

                    // change the button text to uploaded
                    changeUploadButtonText();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Image upload failed. Please try again.");
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    private void changeUploadButtonText() {
        uploadImageButton.setText("IMAGE UPLOADED");
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
