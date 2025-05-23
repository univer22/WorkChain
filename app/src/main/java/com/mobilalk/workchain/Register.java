package com.mobilalk.workchain;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilalk.workchain.helpers.AnimationHelper;
import com.mobilalk.workchain.helpers.NetworkHelper;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.User;

public class Register extends AppCompatActivity {
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText passwordCheckEditText;
    private FirebaseAuth auth;
    private SharedPreferencesHelper sharedPreferences;
    private FirebaseFirestore firestore;
    private CollectionReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        setEditTexts();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, ProjectActivity.class));
        }
        sharedPreferences = new SharedPreferencesHelper(this);
        firestore = FirebaseFirestore.getInstance();
        users = firestore.collection("users");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        setAnimations();
        super.onStart();
    }

    @Override
    protected void onResume() {
        String savedEmail = sharedPreferences.getItem("email", "");
        String savedPassword = sharedPreferences.getItem("password", "");

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
        }

       sharedPreferences.clear();
        super.onResume();
    }

    public void back(View view) {
        finish();
    }

    public void register(View view) {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String email = emailEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordCheck = passwordCheckEditText.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Az emailnek tartalmaznia kell: @ . karaktereket.", Toast.LENGTH_SHORT).show();
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
                    users.add(new User(email, name));
                    Toast.makeText(Register.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, ProjectActivity.class));
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(Register.this, "Hibás email cím formátum!", Toast.LENGTH_SHORT).show();
                    } else if (exception instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Register.this, "Ez az email cím már regisztrálva van.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register.this, "Sikertelen regisztráció!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setEditTexts() {
        emailEditText = findViewById(R.id.emailAddress);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString();
                if (inputText.contains("@") && inputText.contains(".")) {
                    emailEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    emailEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
        nameEditText = findViewById(R.id.name);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    nameEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    nameEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
        passwordEditText = findViewById(R.id.password);
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
        passwordCheckEditText = findViewById(R.id.passwordCheck);
        passwordCheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String password = passwordEditText.getText().toString();
                if (!editable.toString().isEmpty() && editable.toString().equals(password)) {
                    passwordCheckEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    passwordCheckEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
    }

    private void setAnimations() {
        emailEditText.setVisibility(View.INVISIBLE);
        nameEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        passwordCheckEditText.setVisibility(View.INVISIBLE);
        Button register = findViewById(R.id.register);
        register.setVisibility(View.INVISIBLE);
        Button back = findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);

        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.input_animation);
        emailEditText.setVisibility(View.VISIBLE);
        emailEditText.startAnimation(slideInLeft);
        AnimationHelper.delayAnimation(nameEditText, 250, this, R.anim.input_animation);
        AnimationHelper.delayAnimation(passwordEditText, 500, this, R.anim.input_animation);
        AnimationHelper.delayAnimation(passwordCheckEditText, 750, this, R.anim.input_animation);
        AnimationHelper.delayAnimation(register, 1000, this, R.anim.input_animation);
        AnimationHelper.delayAnimation(back, 1250, this, R.anim.input_animation);
    }
}