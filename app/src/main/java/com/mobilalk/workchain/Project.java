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
import com.google.firebase.auth.FirebaseUser;
import com.mobilalk.workchain.helpers.MenuHelper;


public class Project extends AppCompatActivity {

    private FirebaseUser user;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
        }
        MenuHelper.setToolbar(this);

        mainLayout = findViewById(R.id.main);
        addCard("Létrehozott projektek listázása: fejlesztés alatt");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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