package com.example.instagramjr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagramjr.Adapter.CommentsAdapter;
import com.example.instagramjr.Model.Comments;
import com.example.instagramjr.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private EditText comments_edt;
    private Button addCommets;
    private RecyclerView comments_recy;
    private FirebaseFirestore firestore;
    private String post_id;
    private String currentUserId;
    private FirebaseAuth auth;
    private CommentsAdapter adapter;
    private List<Comments> list;
    private List<Users> usersList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comments_edt = findViewById(R.id.comments_edt);
        addCommets = findViewById(R.id.addCommets);
        comments_recy = findViewById(R.id.comments_recy);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        list = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new CommentsAdapter(CommentsActivity.this,list,usersList);

        post_id = getIntent().getStringExtra("postid");

        comments_recy.setHasFixedSize(true);
        comments_recy.setLayoutManager(new LinearLayoutManager(this));
        comments_recy.setAdapter(adapter);

        firestore.collection("Posts/" + post_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        Comments comments = documentChange.getDocument().toObject(Comments.class);
                        String userId = documentChange.getDocument().getString("user");

                        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                           if (task.isSuccessful()){
                               Users users = task.getResult().toObject(Users.class);
                               usersList.add(users);
                               list.add(comments);
                               adapter.notifyDataSetChanged();
                           }else {
                               Toast.makeText(CommentsActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                           }
                            }
                        });

                    }else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });



        addCommets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = comments_edt.getText().toString();
                if (!comment.isEmpty()){
                    Map<String,Object> commentsMap = new HashMap<>();
                    commentsMap.put("comment",comment);
                    commentsMap.put("time", FieldValue.serverTimestamp());
                    commentsMap.put("user",currentUserId);
                    firestore.collection("Posts/" + post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this,"Yorum Eklendi",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(CommentsActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(CommentsActivity.this,"Lütfen Yorum yazın",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}