package com.mobilalk.workchain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

public class Login extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
       setEditTexts();
        setAnimations();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("WorkChainPrefs", MODE_PRIVATE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void back(View view) {
        finish();
    }

    public void login(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Nem email formátum!", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(Login.this, Project.class));
                } else {
                    Toast.makeText(Login.this, "Sikertelen bejelenetkezés!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();
                    Intent intent = new Intent(Login.this, Register.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setEditTexts() {
        emailEditText = findViewById(R.id.email);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString();
                if (inputText.contains("@")) {
                    emailEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    emailEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
        passwordEditText = findViewById(R.id.psw);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 5) {
                    passwordEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    passwordEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
    }

    private void setAnimations() {
        emailEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        Button login = findViewById(R.id.login);
        login.setVisibility(View.INVISIBLE);
        Button back = findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);

        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.input_animation);
        emailEditText.setVisibility(View.VISIBLE);
        emailEditText.startAnimation(slideInLeft);
        AnimationHelper.delayAnimation(passwordEditText, 250, this);
        AnimationHelper.delayAnimation(login, 500, this);
        AnimationHelper.delayAnimation(back, 750, this);

    }

}