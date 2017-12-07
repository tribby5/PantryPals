package pantrypals.database.query;

import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 07/12/17.
 *
 *
 * Purely for testing purposes and reading information from the database, do NOT call any of these unless you are trying to debug and definitely do NOT use to write values.
 *
 */

public class QueryMaker {

    private static final String TAG = "QueryMaker";

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static void getUsers() {
        mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    String userId = childSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getMessage());
            }
        });
    }

    public static void getRecipeCategories() {
        mDatabase.child("/recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    for(DataSnapshot gchild : child.getChildren()) {
                        if(gchild.getKey().equals("category")) {
                            Log.d(TAG, gchild.getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getMessage());
            }
        });
    }

}
