package com.mobilalk.workchain;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilalk.workchain.helpers.MenuHelper;
import com.mobilalk.workchain.helpers.NetworkHelper;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.Task;

public class TaskDetails extends AppCompatActivity {
    private LinearLayout taskLayout;
    private Task task;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference tasks = firestore.collection("tasks");
    private SharedPreferencesHelper sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
        taskLayout = findViewById(R.id.taskContent);
        MenuHelper.setToolbar(this);
        sharedPreferences = new SharedPreferencesHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskLayout.removeAllViews();
        task = loadTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuHelper.onCreateOptionsMenu(menu, getMenuInflater());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MenuHelper.onOptionsItemSelected(item, this);
    }

    private Task loadTask() {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
            finish();
        }
        tasks.document(sharedPreferences.getItem("openedTaskId", "")).get()
                .addOnSuccessListener(documentSnapshot -> {
            Task task = documentSnapshot.toObject(Task.class);
            task.setId(documentSnapshot.getId());
            createContent(task);
        });
        return task;
    }

    private void createContent(Task task) {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        TextView titleText = new TextView(this);
        titleText.setId(View.generateViewId());
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleText.setEms(10);
        titleText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setTextSize(34f);
        titleText.setText(task.getName());

        TextView descriptionText = new TextView(this);
        descriptionText.setId(View.generateViewId());
        descriptionText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        descriptionText.setTypeface(Typeface.MONOSPACE);
        descriptionText.setTextSize(24f);
        descriptionText.setPadding(16, 16, 16, 16);
        String description = "Leírás: " + task.getDescription();
        descriptionText.setText(description);

        TextView dateText = new TextView(this);
        dateText.setId(View.generateViewId());
        dateText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        dateText.setTypeface(Typeface.MONOSPACE);
        dateText.setTextSize(24f);
        dateText.setPadding(16, 16, 16, 16);
        String dueDate = "Határidő: " + task.getDueDate();
        dateText.setText(dueDate);

        TextView priorityText = new TextView(this);
        priorityText.setId(View.generateViewId());
        priorityText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        priorityText.setTypeface(Typeface.MONOSPACE);
        priorityText.setTextSize(24f);
        priorityText.setPadding(16, 16, 16, 16);
        String priority = "Prioritás: " + task.getPriority();
        priorityText.setText(priority);

        Button deleteTask = new Button(this);
        deleteTask.setId(View.generateViewId());
        LinearLayout.LayoutParams deleteTaskParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        deleteTaskParams.topMargin = 30;
        deleteTaskParams.gravity = Gravity.CENTER_HORIZONTAL;
        deleteTask.setLayoutParams(deleteTaskParams);
        deleteTask.setText(getString(R.string.delete));
        deleteTask.setTextSize(24f);
        deleteTask.setAllCaps(false);
        deleteTask.setTextColor(Color.WHITE);
        deleteTask.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));

        deleteTask.setOnClickListener(v -> {
            if (!NetworkHelper.isNetworkAvailable(this)) {
                Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            tasks.document(task.getId()).delete().addOnSuccessListener(deletedSuccessful -> {
                sharedPreferences.addItem("isDeleted", "true");
                finish();
            });
        });

        Button editTask = new Button(this);
        editTask.setId(View.generateViewId());
        LinearLayout.LayoutParams editTaskParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editTaskParams.topMargin = 30;
        editTaskParams.gravity = Gravity.CENTER_HORIZONTAL;
        editTask.setLayoutParams(editTaskParams);
        editTask.setText(getString(R.string.edit));
        editTask.setTextSize(24f);
        editTask.setAllCaps(false);
        editTask.setTextColor(Color.WHITE);
        editTask.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));

        editTask.setOnClickListener(v -> {
            if (!NetworkHelper.isNetworkAvailable(this)) {
                Toast.makeText(this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            sharedPreferences.addItem("editTaskId", task.getId());
            startActivity(new Intent(this, AddTask.class));
        });

        Button back = new Button(this);
        back.setId(View.generateViewId());
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        backParams.topMargin = 30;
        backParams.gravity = Gravity.CENTER_HORIZONTAL;
        back.setLayoutParams(backParams);
        back.setText(getString(R.string.back));
        back.setTextSize(24f);
        back.setAllCaps(false);
        back.setTextColor(Color.WHITE);
        back.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));

        back.setOnClickListener(v -> {
            finish();
        });

        taskLayout.addView(titleText);
        taskLayout.addView(descriptionText);
        taskLayout.addView(dateText);
        taskLayout.addView(priorityText);
        taskLayout.addView(deleteTask);
        taskLayout.addView(editTask);
        taskLayout.addView(back);
    }
}