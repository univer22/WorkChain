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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilalk.workchain.helpers.MenuHelper;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.Project;

public class AddProject extends AppCompatActivity {

    private EditText projectNameEditText;
    private EditText descriptionEdittext;
    private FirebaseFirestore firestore;
    private CollectionReference projects;
    private SharedPreferencesHelper sharedPreferences;


    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_project);

        if (auth.getCurrentUser() == null) {
            finish();
        }
        MenuHelper.setToolbar(this);
        setEditTexts();
        firestore = FirebaseFirestore.getInstance();
        projects = firestore.collection("projects");
        sharedPreferences = new SharedPreferencesHelper(this);
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
        String name = projectNameEditText.getText().toString().trim();
        String description = descriptionEdittext.getText().toString().trim();
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }
        projects.add(new Project(name, description, auth.getUid())).addOnSuccessListener(documentReference -> {
            sharedPreferences.addItem("newProjectId", documentReference.getId());
            finish();
        });
    }

    public void back(View view) {
        finish();
    }

    private void setEditTexts() {
        projectNameEditText = findViewById(R.id.projectName);
        projectNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

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
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

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