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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobilalk.workchain.helpers.AnimationHelper;
import com.mobilalk.workchain.helpers.MenuHelper;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.Project;
import com.mobilalk.workchain.models.Task;

public class ProjectDetails extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private SharedPreferencesHelper sharedPreferences;
    private LinearLayout mainLayout;
    private Project project;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference projects = firestore.collection("projects");
    private CollectionReference tasks = firestore.collection("tasks");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (auth.getCurrentUser() == null) {
            finish();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_details);
        MenuHelper.setToolbar(this);
        sharedPreferences = new SharedPreferencesHelper(this);
        mainLayout = findViewById(R.id.main);
        project = loadProject();
        listTasks();
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

    @Override
    protected void onResume() {
        String newTaskId = sharedPreferences.getItem("newTaskId", "");
        if (!newTaskId.isEmpty()) {
            tasks.whereEqualTo("projectId", sharedPreferences.getItem("openedProjectID", ""))
                    .get().addOnSuccessListener(documentSnapshot -> {
                        for (QueryDocumentSnapshot document : documentSnapshot) {
                            if  (document.getId().equals(newTaskId)) {
                                Task task = document.toObject(Task.class);
                                task.setId(document.getId());
                                sharedPreferences.deleteItem("newTaskId");
                                addCard(task);
                                break;
                            }
                        }
            });
        }
        if (!sharedPreferences.getItem("isDeleted", "").isEmpty()) {
            removeTaskFromView(sharedPreferences.getItem("openedTaskId", ""));
            sharedPreferences.deleteItem("openedTaskId");
            sharedPreferences.deleteItem("isDeleted");
        }
        super.onResume();
    }

    private void addHeaderAndButton(Project project) {
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
        titleText.setText(project.getName());

        TextView descriptionText = new TextView(this);
        descriptionText.setId(View.generateViewId());
        descriptionText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        descriptionText.setTypeface(Typeface.MONOSPACE);
        descriptionText.setTextSize(24f);
        descriptionText.setPadding(16, 16, 16, 16);
        descriptionText.setText(project.getDescription());

        Button newTask = new Button(this);
        newTask.setId(View.generateViewId());
        LinearLayout.LayoutParams newTaskParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        newTaskParams.topMargin = 30;
        newTaskParams.gravity = Gravity.CENTER_HORIZONTAL;
        newTask.setLayoutParams(newTaskParams);
        newTask.setText(getString(R.string.newTask));
        newTask.setTextSize(24f);
        newTask.setAllCaps(false);
        newTask.setTextColor(Color.WHITE);
        newTask.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));

        newTask.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTask.class));
        });

        mainLayout.addView(titleText);
        mainLayout.addView(descriptionText);
        mainLayout.addView(newTask);
    }

    private Project loadProject() {
        projects.document(sharedPreferences.getItem("openedProjectID", "")).get().addOnSuccessListener(documentSnapshot -> {
            Project project = documentSnapshot.toObject(Project.class);
            project.setId(documentSnapshot.getId());
            addHeaderAndButton(project);
        });
        return project;
    }

    private void listTasks() {
        tasks.whereEqualTo("projectId", sharedPreferences.getItem("openedProjectID", ""))
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Task task = document.toObject(Task.class);
                task.setId(document.getId());
                addCard(task);
            }
        });
    }

    private void addCard(Task task) {
        CardView cardView = new CardView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(16, 16, 16, 16);

        cardView.setLayoutParams(layoutParams);
        cardView.setCardElevation(4f);
        cardView.setPadding(16, 16, 16, 16);
        cardView.setTag(task.getId());

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView textView = new TextView(this);
        textView.setText(task.getName());
        textView.setTextSize(18f);
        textView.setPadding(16, 16, 16, 16);
        contentLayout.addView(textView);

        Button button = new Button(this);
        button.setText(getString(R.string.open));
        button.setPadding(16, 16, 16, 16);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
        button.setOnClickListener(v -> {
            sharedPreferences.addItem("openedTaskId", task.getId());
            startActivity(new Intent(this, TaskDetails.class));
        });
        contentLayout.addView(button);
        cardView.addView(contentLayout);
        mainLayout.addView(cardView);
        cardView.setVisibility(View.INVISIBLE);
        AnimationHelper.delayAnimation(cardView, 150 * mainLayout.getChildCount() - 1, this, R.anim.projects_and_tasks);
    }

    private void removeTaskFromView(String taskId) {
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View view = mainLayout.getChildAt(i);
            if (view instanceof CardView && view.getTag() != null && view.getTag().equals(taskId)) {
                mainLayout.removeViewAt(i);
                return;
            }
        }
    }
}