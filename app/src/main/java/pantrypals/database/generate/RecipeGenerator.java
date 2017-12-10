package pantrypals.database.generate;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pantrypals.models.Recipe;

/**
 * RecipeGenerator reads a JSON array from the local system and generates a Recipe object that can
 * be serialized to be pushed to Firebase Realtime Database
 *
 * Created by Hunter Lee on 11/9/2017.
 */

public class RecipeGenerator {

    private String testJSON = "[\n"+
            "  {\n"+
            "    \"title\": \"Butternut and Acorn Squash Soup\",\n"+
            "    \"text\": \"1. Preheat oven to 350 degrees F (175 degrees C). Place the squash halves cut side down in a baking dish. Bake 45 minutes, or until tender. Remove from heat, and cool slightly. Scoop the pulp from the skins. Discard skins.\\n  2. Melt the butter in a skillet over medium heat, and saute the onion until tender.\\n  3. In a blender or food processor, blend the squash pulp, onion, broth, brown sugar, cream cheese, pepper, and cinnamon until smooth. This may be done in several batches.\\n  4. Transfer the soup to a pot over medium heat, and cook, stirring occasionally, until heated through. Garnish with parsley, and serve warm.\",\n"+
            "    \"requiredIngredients\": [\n"+
            "      {\n"+
            "        \"name\": \"Butternut Squash\",\n"+
            "        \"amount\": 1,\n"+
            "        \"unit\": \"pcs\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Acorn Squash\",\n"+
            "        \"amount\": 1,\n"+
            "        \"unit\": \"pcs\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Butter\",\n"+
            "        \"amount\": 3,\n"+
            "        \"unit\": \"tablespoons\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Sweet Onion\",\n"+
            "        \"amount\": 0.25,\n"+
            "        \"unit\": \"cup\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Chicken Broth\",\n"+
            "        \"amount\": 1,\n"+
            "        \"unit\": \"quart\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Brown sugar\",\n"+
            "        \"amount\": 0.33,\n"+
            "        \"unit\": \"cup\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Cream Cheese\",\n"+
            "        \"amount\": 8,\n"+
            "        \"unit\": \"Ounce\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Ground Black Pepper\",\n"+
            "        \"amount\": 0.5,\n"+
            "        \"unit\": \"teaspoon\"\n"+
            "      },\n"+
            "      {\n"+
            "        \"name\": \"Parsley\",\n"+
            "        \"amount\": 5,\n"+
            "        \"unit\": \"grams\"\n"+
            "      }\n"+
            "    ]\n"+
            "  }\n"+
            "]";

    private DatabaseReference ref;

    public RecipeGenerator(DatabaseReference ref) {
        this.ref = ref;
    }

    public List<Recipe> generateRecipes(String path) {
//        JSONParser parser = new JSONParser();
        List<Recipe> result = new ArrayList<>();
        JSONArray recipes = null;
        try {
            recipes = new JSONArray(testJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            recipes = (JSONArray) parser.parse(new FileReader(path));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        try {
            int length = recipes.length();
            for (int i = 0; i < length; i++) {
                JSONObject r = recipes.getJSONObject(i);
                Recipe recipe = new Recipe();
                Map<String, Recipe.Ingredient> requiredIngredients = new HashMap<>();

                String title = r.getString("title");
                String text = r.getString("text");

                JSONArray ingArray = r.getJSONArray("requiredIngredients");
                int ing_length = ingArray.length();
                for (int j = 0; j < ing_length; j++) {
                    JSONObject ingredient_JSON = ingArray.getJSONObject(j);
                    Recipe.Ingredient ingredient = new Recipe.Ingredient();
                    String name = ingredient_JSON.getString("name");
                    String unit = ingredient_JSON.getString("unit");
                    Double amount = ingredient_JSON.getDouble("amount");
                    ingredient.setName(name);
                    ingredient.setUnit(unit);
                    ingredient.setAmount(amount);
                    requiredIngredients.put(name, ingredient);
                }

                recipe.setTitle(title);
                recipe.setText(text);
                //recipe.setRequiredIngredients(requiredIngredients);
                //recipe.setTimestamp(recipe.generateTimestamp());
                result.add(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
