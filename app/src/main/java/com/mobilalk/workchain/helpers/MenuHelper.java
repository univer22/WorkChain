package com.mobilalk.workchain.helpers;


import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mobilalk.workchain.MainActivity;
import com.mobilalk.workchain.ProjectActivity;
import com.mobilalk.workchain.R;

public class MenuHelper {

    public static void setToolbar(AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    public static boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public static boolean onOptionsItemSelected(MenuItem item, Context context) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(context, "Sikeres kijelentkezés", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, MainActivity.class));
            new NotificationHelper(context).send("Amíg nem jelentkezel be nem kapsz értesítést a határidőkről. ");
            ((AppCompatActivity)context).finish();
            return true;
        }
        else if (itemId == R.id.home) {
            context.startActivity(new Intent(context, ProjectActivity.class));
            ((AppCompatActivity)context).finish();
            return true;
        }
        return false;
    }
}

