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

public class LoginActivity extends AppCompatActivity {

    private EditText emailgn,passwordlgn;
    private Button loginbttn;
    private TextView signuptx;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailgn = findViewById(R.id.emaillgn);
        passwordlgn = findViewById(R.id.passwordlgn);
        loginbttn = findViewById(R.id.loginbttn);
        signuptx = findViewById(R.id.signuptx);

        signuptx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        loginbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailgn.getText().toString();
                String pass = passwordlgn.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Giriş başarılı.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }else {
                    Toast.makeText(LoginActivity.this,"Lütfen email ve şifrenizi girin.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}