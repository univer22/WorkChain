package com.mobilalk.workchain;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.mobilalk.workchain.helpers.MenuHelper;

public class AddProject extends AppCompatActivity {

    private EditText projectNameEditText;
    private EditText descriptionEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_project);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
        MenuHelper.setToolbar(this);
        setEditTexts();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuHelper.onCreateOptionsMenu(menu, getMenuInflater());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MenuHelper.onOptionsItemSelected(item, this);
    }

    public void create(View view) {
        Toast.makeText(this, "Készül :)", Toast.LENGTH_SHORT).show();
        finish();
    }
    public void back(View view) {
        finish();
    }

    private void setEditTexts() {
        projectNameEditText = findViewById(R.id.projectName);
        projectNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    projectNameEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    projectNameEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
        descriptionEdittext = findViewById(R.id.description);
        descriptionEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    descriptionEdittext.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    descriptionEdittext.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
    }
}