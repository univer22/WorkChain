package com.mobilalk.workchain;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobilalk.workchain.helpers.MenuHelper;
import com.mobilalk.workchain.helpers.SharedPreferencesHelper;
import com.mobilalk.workchain.models.Project;


public class ProjectActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private LinearLayout mainLayout;
    private FirebaseFirestore firestore;
    private CollectionReference projects;
    private SharedPreferencesHelper sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);
        if (auth.getCurrentUser() == null) {
            finish();
        }
        MenuHelper.setToolbar(this);
        firestore = FirebaseFirestore.getInstance();
        projects = firestore.collection("projects");
        listProjects();
        sharedPreferences = new SharedPreferencesHelper(this);
        mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        String newProjectId = sharedPreferences.getItem("newProjectId", "");
        if (!newProjectId.isEmpty()) {
            projects.document(newProjectId).get().addOnSuccessListener(documentSnapshot -> {
                Project project = documentSnapshot.toObject(Project.class);
                addCard(project.getName() + " " + project.getDescription());
                sharedPreferences.deleteItem("newProjectId");
            });
        }
        super.onResume();

    }

    private void listProjects() {
        projects.whereEqualTo("userID", auth.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
               Project project = document.toObject(Project.class);
               addCard(project.getName() + " " + project.getDescription());
           }
        });
    }

    public void newProject(View view) {
        startActivity(new Intent(this, AddProject.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuHelper.onCreateOptionsMenu(menu, getMenuInflater());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MenuHelper.onOptionsItemSelected(item, this);
    }

    private void addCard(String text) {
        CardView cardView = new CardView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(16, 16, 16, 16);

        cardView.setLayoutParams(layoutParams);
        cardView.setCardElevation(4f);
        cardView.setPadding(16, 16, 16, 16);

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(18f);
        textView.setPadding(16, 16, 16, 16);

        cardView.addView(textView);
        mainLayout.addView(cardView);
    }
}