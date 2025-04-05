package com.mobilalk.workchain;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.Task;

public class AddTask extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore =  FirebaseFirestore.getInstance();;
    private CollectionReference tasks = firestore.collection("tasks");
    private SharedPreferencesHelper sharedPreferences;

    private EditText nameEditText;
    private EditText dateEditText;
    private EditText descriptionEditText;
    private RadioGroup priorityGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);
        if (auth.getCurrentUser() == null) {
            finish();
        }
        setEditTexts();
        sharedPreferences = new SharedPreferencesHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void back(View view) {
        finish();
    }

    public void createTask(View view) {
        String name = nameEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        int selectedPriorityId = priorityGroup.getCheckedRadioButtonId();
        if (name.isEmpty() || date.isEmpty() || description.isEmpty() || selectedPriorityId == -1) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedPriorityButton = findViewById(selectedPriorityId);
        String priority = selectedPriorityButton.getText().toString();

        tasks.add(new Task(name, date, description, priority, sharedPreferences.getItem("openedProjectID", "")))
                .addOnSuccessListener(documentReference -> {
            sharedPreferences.addItem("newTaskId", documentReference.getId());
            finish();
        });

    }

    private void setEditTexts() {
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
        dateEditText = findViewById(R.id.date);
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    dateEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    dateEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
        descriptionEditText = findViewById(R.id.description);
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    descriptionEditText.setBackgroundResource(R.drawable.editttext_filled);
                } else {
                    descriptionEditText.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
        priorityGroup = findViewById(R.id.priorityGroup);
    }
}