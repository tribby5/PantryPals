package pantrypals.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.databaes.pantrypals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pantrypals.activities.HomeActivity;
import pantrypals.models.Post;

public class HomeFragment extends Fragment {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String userId;

    private ListView feedListView;
    private ArrayList<Post> feedList = new ArrayList<>();
    private ArrayList<String> tempList = new ArrayList<>();
    private ArrayAdapter<String> feedListViewAdapter;
    private String oldestPostId;

    public HomeFragment() {
        // Required empty public constructor

        // TODO: Use the following current user information for more intelligent feed later on
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Retrieve data from Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        ref = mFirebaseDatabase.getReference("/posts");
        ref.limitToFirst(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    oldestPostId = snapshot.getKey();

                    dataSnapshot.getChildrenCount();
                    Post post = snapshot.getValue(Post.class);
                    String postId = snapshot.getKey();
                    feedList.add(post);
                    tempList.add(postId);
                    Log.e("Retrieved postId: ", postId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the recipe_layout first
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        feedListView = (ListView) view.findViewById(R.id.feedListView);
        feedListViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                tempList
        );
        feedListView.setAdapter(feedListViewAdapter);

        // Define click actions
//        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                if (position == 0) {
//
//                }
//            }
//        });


        return view;
    }


    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
