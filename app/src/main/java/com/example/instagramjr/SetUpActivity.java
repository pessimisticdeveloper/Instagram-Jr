package com.example.instagramjr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView circlev;
    private EditText profile_name;
    private Button savebtn;

    private Toolbar setuptoolbar;
    private FirebaseAuth auth;
    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private String Uid;
    private ProgressBar progressBar;

    private Uri imageUri = null;
    private FirebaseStorage storage;

    private boolean isPhotoSelecd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        setuptoolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setuptoolbar);
        getSupportActionBar().setTitle("Profil");

        circlev = findViewById(R.id.circlev);
        profile_name = findViewById(R.id.comments_edt);
        savebtn = findViewById(R.id.addCommets);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance("gs://instagramjr-ae6c6.appspot.com");
        storageRef = storage.getReference();
        firestore = FirebaseFirestore.getInstance();
        Uid = auth.getCurrentUser().getUid();

        progressBar.setVisibility(View.INVISIBLE);

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUrl = task.getResult().getString("image");
                        profile_name.setText(name);
                        imageUri = Uri.parse(imageUrl);
                        Glide.with(SetUpActivity.this).load(imageUrl).into(circlev);
                    }
                }
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name = profile_name.getText().toString();
                StorageReference imageRef = storageRef.child("Profile_pics").child(Uid + ".jpg");
                if (isPhotoSelecd){
                    if (!name.isEmpty() && imageUri != null) {
                        imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            saveToFireStore(task, name, uri);
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SetUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SetUpActivity.this, "Lütfen isim ve resminizi seçiniz..", Toast.LENGTH_SHORT).show();
                    }
                }else {
                  saveToFireStore(null,name,imageUri);
                }}
        });

        circlev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SetUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        openGallery();
                    }
                }
            }
        });
    }

    private void saveToFireStore(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("image",downloadUri.toString());
        firestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SetUpActivity.this,"Profil Ayarları kayıt edildi.",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetUpActivity.this,MainActivity.class));
                    finish();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SetUpActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
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
            Uri imageUri = data.getData();
            circlev.setImageURI(imageUri);

            isPhotoSelecd = true;

        }
    }
}