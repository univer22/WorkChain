package com.mobilalk.workchain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobilalk.workchain.helpers.NotificationHelper;
import com.mobilalk.workchain.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference tasks = firestore.collection("tasks");
    private final CollectionReference projects = firestore.collection("projects");
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    public void onReceive(Context context, Intent intent) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        projects.whereEqualTo("userID", auth.getUid()).get().addOnSuccessListener(projectSnapshot -> {
            List<String> projectIds = new ArrayList<>();
            for (QueryDocumentSnapshot projectDoc : projectSnapshot) {
                projectIds.add(projectDoc.getId());
            }

            if (projectIds.isEmpty()) {
                return;
            }

            tasks.get().addOnSuccessListener(taskSnapshot -> {
                for (QueryDocumentSnapshot taskDoc : taskSnapshot) {
                    Task task = taskDoc.toObject(Task.class);
                    if (projectIds.contains(task.getProjectId()) && isDueSoon(task.getDueDate())) {
                        new NotificationHelper(context).send("Közelgő határidő: " + task.getName());
                    }
                }
            });
        });
    }

    private boolean isDueSoon(String dueDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date due = dateFormat.parse(dueDate);
            Date now = new Date();
            long difference = due.getTime() - now.getTime();
            return difference <= 72 * 60 * 60 * 1000;
        } catch (ParseException ignored) {}
        return false;
    }
}