package com.example.instagramjr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button postBtn;
    private EditText postxt;
    private ImageView post_image;
    private ProgressBar post_progressbar;
    private final Uri postImageuri = null;
    private FirebaseAuth auth;
    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private String cuurentUserId ;
    private Toolbar posttoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        postBtn = findViewById(R.id.postBtn);
        postxt = findViewById(R.id.postxt);
        post_image = findViewById(R.id.post_image);
        post_progressbar = findViewById(R.id.post_progressbar);
        post_progressbar.setVisibility(View.INVISIBLE);

        posttoolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(posttoolbar);
        getSupportActionBar().setTitle("Post Ekle");

        auth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance("gs://instagramjr-ae6c6.appspot.com");
        storageRef = storage.getReference();
        firestore = FirebaseFirestore.getInstance();
        cuurentUserId = auth.getCurrentUser().getUid();

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_progressbar.setVisibility(View.VISIBLE);
                String caption = postxt.getText().toString();
                if (!caption.isEmpty() && postImageuri != null){
                    StorageReference postRef = storageRef.child("post_images").child(FieldValue.serverTimestamp().toString() + "jpg");
                    postRef.putFile(postImageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String,Object> postMap = new HashMap<>();
                                        postMap.put("image",uri.toString());
                                        postMap.put("user",cuurentUserId);
                                        postMap.put("caption",caption);
                                        postMap.put("time",FieldValue.serverTimestamp());

                                        firestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    post_progressbar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddPostActivity.this,"Post ekleme Bşarılı",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AddPostActivity.this,MainActivity.class));
                                                    finish();
                                                }else {
                                                    post_progressbar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddPostActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }else {
                                Toast.makeText(AddPostActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    post_progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddPostActivity.this,"Lütfen foto ve yazı ekleyin.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Resim Seç"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri postImageuri = data.getData();
            post_image.setImageURI(postImageuri);


        }
    }
}