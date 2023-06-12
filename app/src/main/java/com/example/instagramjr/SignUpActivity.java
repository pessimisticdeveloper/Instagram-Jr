package com.example.instagramjr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, password;
    private Button signupbtn;
    private TextView signintxt;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupbtn = findViewById(R.id.signupbtn);
        signintxt = findViewById(R.id.signintxt);

        signintxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtxt = email.getText().toString();
                String passtxt = password.getText().toString();

                if (!emailtxt.isEmpty() && !passtxt.isEmpty()){
                    auth.createUserWithEmailAndPassword(emailtxt,passtxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"Kayıt Başarılı",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,SetUpActivity.class));
                                finish();
                            }else {
                                Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this,"Lütfen email ve şifrenizi giriniz...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}