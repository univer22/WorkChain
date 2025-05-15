package com.mobilalk.workchain;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilalk.workchain.helpers.NetworkHelper;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddTask extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore =  FirebaseFirestore.getInstance();;
    private CollectionReference tasks = firestore.collection("tasks");
    private SharedPreferencesHelper sharedPreferences;

    private EditText nameEditText;
    private EditText dateEditText;
    private EditText descriptionEditText;
    private RadioGroup priorityGroup;

    private String name;
    private String date;
    private String description;
    private int selectedPriorityId;
    private RadioButton selectedPriorityButton;
    private String priority;


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

    @Override
    protected void onResume() {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!sharedPreferences.getItem("editTaskId", "").isEmpty()) {
            TextView mainText = findViewById(R.id.mainText);
            mainText.setText(getString(R.string.edit));
            Button addTaskButton = findViewById(R.id.addTask);
            addTaskButton.setText(R.string.save);
            addTaskButton.setOnClickListener(view -> saveEditedTask());
            tasks.document(sharedPreferences.getItem("editTaskId", "")).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Task task = documentSnapshot.toObject(Task.class);
                        task.setId(documentSnapshot.getId());
                        nameEditText.setText(task.getName());
                        dateEditText.setText(task.getDueDate());
                        descriptionEditText.setText(task.getDescription());
                        String priority = task.getPriority();
                        int priorityId = getPriorityRadioButtonId(priority);
                        RadioButton priorityRadioButton = findViewById(priorityId);
                        priorityRadioButton.setChecked(true);
                    });
        }
        super.onResume();
    }

    public void back(View view) {
        sharedPreferences.deleteItem("editTaskId");
        finish();
    }

    public void createTask(View view) {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!setInputTextValues()) {
            return;
        }
        tasks.add(new Task(name, date, description, priority, sharedPreferences.getItem("openedProjectID", "")))
                .addOnSuccessListener(documentReference -> {
            sharedPreferences.addItem("newTaskId", documentReference.getId());
            finish();
        });

    }

    private int getPriorityRadioButtonId(String priority) {
        switch (priority) {
            case "Magas":
                return R.id.high;
            case "Közepes":
                return R.id.medium;
            case "Alacsony":
                return R.id.low;
        }
        return -1;
    }

    private void saveEditedTask() {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!setInputTextValues()) {
            return;
        }
        tasks.document(sharedPreferences.getItem("editTaskId", ""))
                .set(new Task(name, date, description, priority, sharedPreferences.getItem("openedProjectID", "")))
                .addOnSuccessListener(v -> {
                    sharedPreferences.deleteItem("editTaskId");
                    Toast.makeText(this, "Sikeres módosítás!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private boolean setInputTextValues() {
        name = nameEditText.getText().toString().trim();
        date = dateEditText.getText().toString().trim();
        description = descriptionEditText.getText().toString().trim();

        selectedPriorityId = priorityGroup.getCheckedRadioButtonId();
        if (name.isEmpty() || date.isEmpty() || description.isEmpty() || selectedPriorityId == -1) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDate(date)) {
            Toast.makeText(this, "Hibás dátumformátum! (éééé-hh-nn)", Toast.LENGTH_SHORT).show();
            return false;
        }

        selectedPriorityButton = findViewById(selectedPriorityId);
        priority = selectedPriorityButton.getText().toString();
        return true;
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        simpleDateFormat.setLenient(false);

        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException exception) {
            return false;
        }
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
                if (isValidDate(editable.toString())) {
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