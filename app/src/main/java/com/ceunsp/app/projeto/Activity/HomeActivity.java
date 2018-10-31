package com.ceunsp.app.projeto.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ceunsp.app.projeto.Fragments.AboutFragment;
import com.ceunsp.app.projeto.Fragments.HomeFragment;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    public TextView userNicknameTextView, userEmailTextView;
    private static final String PREFERENCES = "Preferences";
    private boolean accountSettingsIsEnable = false;
    private SharedPreferences preferences;
    public CircleImageView userImageView;
    private FloatingActionMenu menu;
    private boolean exit = false;

    @Override
    protected void onResume() {
        retrieveProfilePhoto();
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        preferences = getSharedPreferences(PREFERENCES, 0);

        View headerView = navigationView.getHeaderView(0);
        userNicknameTextView = headerView.findViewById(R.id.user_nickname_TextView);
        userEmailTextView = headerView.findViewById(R.id.user_email_TextView);
        userImageView = headerView.findViewById(R.id.user_imageView);

        userNicknameTextView.setText(preferences.getString("nickname", ""));
        userEmailTextView.setText(preferences.getString("email", ""));

        HomeFragment homeFragment = new HomeFragment
                (preferences.getString("userType", ""));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, homeFragment);
        transaction.commit();

        menu = findViewById(R.id.float_menu);
        menu.showMenu(true);
        menu.hideMenu(true);
        menu.toggleMenu(true);
        menu.setElevation(30);
    }

    @Override
    protected void onStart() {
        super.onStart();
        menu.setVisibility(View.VISIBLE);
        exit = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            exit = false;

        } else {
            if (exit) {
                this.finishAffinity();
            } else {
                Toast.makeText(getApplicationContext(), "Pressione novamente para encerrar.",
                        Toast.LENGTH_LONG).show();
                exit = true;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.today) {
            menu.setVisibility(View.VISIBLE);
            HomeFragment homeFragment = new HomeFragment
                    (preferences.getString("userType", ""));

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, homeFragment);
            transaction.commit();

        } else if (id == R.id.nav_schedule) {

            if (preferences.getString("userType", "").equals("Aluno")) {
                loadStudentClass(preferences);

            } else if (preferences.getString("userType", "").equals("Professor")) {
                Intent teacherIntent = new Intent(getApplicationContext(), TeacherClassesActivity.class);
                startActivity(teacherIntent);
                finish();

            }

        } else if (id == R.id.nav_annotation) {
            Intent intent = new Intent(getApplicationContext(), AnnotationsActivity.class);
            intent.putExtra("alteracao", "nao");
            startActivity(intent);

        } else if (id == R.id.nav_info) {

            menu.setVisibility(View.GONE);
            AboutFragment aboutFragment = new AboutFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, aboutFragment);
            transaction.commit();


        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            removeAllPreferences(editor);
            firebaseHelper.getAuth().signOut();
            Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLogin);
            finish();

        } else if (id == R.id.nav_exit) {
            finishAffinity();

        } else if (id == R.id.nav_account_settings) {
            if (accountSettingsIsEnable) {

                Intent intentUserSettings = new Intent(getApplicationContext(), RegisterActivity.class);
                intentUserSettings.putExtra("operation", "View&Edit");
                intentUserSettings.putExtra("name", preferences.getString("name", ""));
                intentUserSettings.putExtra("lastName", preferences.getString("lastName", ""));
                intentUserSettings.putExtra("nickname", preferences.getString("nickname", ""));
                intentUserSettings.putExtra("dateOfBirth", preferences.getString("dateOfBirth", ""));
                intentUserSettings.putExtra("userType", preferences.getString("userType", ""));
                intentUserSettings.putExtra("email", preferences.getString("email", ""));

                intentUserSettings.putExtra("photo", convertImageViewToByteArray(userImageView));
                startActivity(intentUserSettings);

            } else {

                Toast.makeText(getApplicationContext(),
                        "Falha ao carregar os dados, tente novamente",
                        Snackbar.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public byte[] convertImageViewToByteArray(CircleImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    public void loadStudentClass(SharedPreferences preferences) {

        if (preferences.getString("classID", "").equals("")) {
            Intent intentNewClass = new Intent(getApplicationContext(), StudentClassActivity.class);
            startActivity(intentNewClass);
            finish();
        } else {
            Intent intentCalendar = new Intent(getApplicationContext(), CalendarMainActivity.class);
            intentCalendar.putExtra("classID", preferences.getString("classID", ""));
            startActivity(intentCalendar);
            finish();
        }
    }

    public void removeAllPreferences(SharedPreferences.Editor editor) {

        editor.remove("userID");
        editor.remove("name");
        editor.remove("lastName");
        editor.remove("nickname");
        editor.remove("dateOfBirth");
        editor.remove("userType");
        editor.remove("classID");
        editor.remove("course");
        editor.remove("college");
        editor.remove("email");
        editor.apply();
        editor.commit();
    }

    public void retrieveProfilePhoto() {

        final Bitmap[] bitmap = new Bitmap[1];

        StorageReference storageRef = firebaseHelper.getStorage();
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child("profilePicture." + firebaseHelper.getUserID()).getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        userImageView.setImageBitmap(bitmap[0]);
                        accountSettingsIsEnable = true;

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                userImageView.setImageResource(R.drawable.default_profile_image);
                accountSettingsIsEnable = true;
            }
        });

    }

    public void openAnonnationView(View view) {

        Intent intentAnnotation = new Intent(getApplicationContext(), NewAnnotationActivity.class);
        intentAnnotation.putExtra("alteracao", "nao");
        startActivity(intentAnnotation);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void openEventView(View view) {

        String userType = preferences.getString("userType", "");

        if (userType.equals("Professor")) {

            Intent teacherIntent = new Intent(getApplicationContext(), TeacherClassesActivity.class);
            teacherIntent.putExtra("sender", "homeActivity");
            startActivity(teacherIntent);
            finish();


        } else if (userType.equals("Aluno")) {

            if (preferences.getString("classID", "").equals("")) {
                Intent intentNewClass = new Intent(getApplicationContext(), StudentClassActivity.class);
                startActivity(intentNewClass);
                finish();

            } else {

                Intent intentEvent = new Intent(getApplicationContext(), EventBodyActivity.class);
                intentEvent.putExtra("sender", "homeActivity");
                intentEvent.putExtra("operation", "create");
                intentEvent.putExtra("date", System.currentTimeMillis());
                intentEvent.putExtra("userClassID", preferences.getString("classID", ""));
                intentEvent.putExtra("userID", firebaseHelper.getUserID());
                finish();
                startActivity(intentEvent);

            }
        }
    }
}