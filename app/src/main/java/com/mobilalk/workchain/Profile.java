package com.mobilalk.workchain;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilalk.workchain.helpers.MenuHelper;
import com.mobilalk.workchain.helpers.PermissionHelper;
import com.mobilalk.workchain.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class Profile extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference users = firestore.collection("users");
    private User user;
    private LinearLayout mainLayout;

    private final int RESULT_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        if (auth.getCurrentUser() == null) {
            finish();
        }
        new PermissionHelper().storageAndCameraPermission(this, this);
        MenuHelper.setToolbar(this);
        mainLayout = findViewById(R.id.main);
        loadUser(loaded -> {
            user = loaded;
            addHeaderAndButton(user);
            setProfileImageIfAvaliable();
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Nem adtál engedélyt", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != RESULT_OK || user == null) {
                return;
            }
            if (requestCode == RESULT_CODE) {
                if (data.getExtras() != null && data.getExtras().get("data") != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    saveImageAndUpdateProfile(image);
                } else if(data.getData() != null) {
                    try {
                        Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        saveImageAndUpdateProfile(image);
                    } catch (IOException ignored) {}
                }
            }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = (User) savedInstanceState.getSerializable("user");
    }


    private void saveImageAndUpdateProfile(Bitmap image) {
        String filename = "profile_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();

            String filePath = file.getAbsolutePath();
            user.setPhotoUrl(filePath);
            setProfileImageIfAvaliable();

            users.whereEqualTo("email", user.getEmail()).get()
                    .addOnSuccessListener(querySnapshot -> {
                        String docId = querySnapshot.getDocuments().get(0).getId();
                        users.document(docId)
                                .update("photoUrl", filePath);
                    });

        } catch (IOException ignored) {}
    }

    private void setProfileImageIfAvaliable() {
        ImageView profileImage = findViewById(R.id.profilePhoto);
        if (user.getPhotoUrl() != null) {
            profileImage.setImageBitmap(BitmapFactory.decodeFile(user.getPhotoUrl()));
        }
    }

    private void addHeaderAndButton(User user) {
        TextView nameText = new TextView(this);
        nameText.setId(View.generateViewId());
        nameText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        nameText.setTypeface(Typeface.MONOSPACE);
        nameText.setTextSize(24f);
        nameText.setPadding(16, 16, 16, 16);
        nameText.setText(user.getName());


        Button openCamera = new Button(this);
        openCamera.setId(View.generateViewId());
        LinearLayout.LayoutParams openCameraParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        openCameraParams.topMargin = 30;
        openCameraParams.gravity = Gravity.CENTER_HORIZONTAL;
        openCamera.setLayoutParams(openCameraParams);
        openCamera.setText(getString(R.string.openCamera));
        openCamera.setTextSize(24f);
        openCamera.setAllCaps(false);
        openCamera.setTextColor(Color.WHITE);
        openCamera.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));

        openCamera.setOnClickListener(v -> {
            takePhoto();
        });

        Button uploadPhoto = new Button(this);
        uploadPhoto.setId(View.generateViewId());
        LinearLayout.LayoutParams uploadPhotoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        uploadPhotoParams.topMargin = 30;
        uploadPhotoParams.gravity = Gravity.CENTER_HORIZONTAL;
        uploadPhoto.setLayoutParams(uploadPhotoParams);
        uploadPhoto.setText(getString(R.string.uploadPhoto));
        uploadPhoto.setTextSize(24f);
        uploadPhoto.setAllCaps(false);
        uploadPhoto.setTextColor(Color.WHITE);
        uploadPhoto.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));

        uploadPhoto.setOnClickListener(v -> {
            uploadPhoto();
        });

        mainLayout.addView(nameText);
        mainLayout.addView(openCamera);
        mainLayout.addView(uploadPhoto);
    }

    private void loadUser(Consumer<User> callback) {
        users.whereEqualTo("email", auth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User loadedUser = documentSnapshot.getDocuments().get(0).toObject(User.class);
                    callback.accept(loadedUser);
                });
    }

    private void takePhoto() {
        startActivityForResult(new Intent().setAction(MediaStore.ACTION_IMAGE_CAPTURE), RESULT_CODE);
    }

    private void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_CODE);
    }
}