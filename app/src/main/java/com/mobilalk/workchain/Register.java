package com.mobilalk.workchain;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText passwordCheckEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getIntExtra("SECRET_KEY", 0) != SecretKeys.REGISTER_SECRET_KEY) {
            finish();
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailAddress);
        nameEditText = findViewById(R.id.name);
        passwordEditText = findViewById(R.id.password);
        passwordCheckEditText = findViewById(R.id.passwordCheck);
        auth = FirebaseAuth.getInstance();
    }

    public void back(View view) {
        finish();
    }

    public void register(View view) {
        String email = emailEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordCheck = passwordCheckEditText.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordCheck)) {
            Toast.makeText(this, "A két jelszó nem egyezik!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "A jelszónak legalább 6 karakternek kell lennie.", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();

                    //TODO: move next acitivy
                } else {
                    Toast.makeText(Register.this, "Sikertelen regisztráció!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}