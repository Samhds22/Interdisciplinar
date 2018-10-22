package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    public TextView userNicknameTextView, userEmailTextView;
    private static final String PREFERENCES = "Preferences";
    SharedPreferences preferences;
    public ImageView userImageView;
    private boolean exit = false;

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

        /*userNicknameTextView = drawer.findViewById(R.id.user_nickname_TextView);
        userEmailTextView    = drawer.findViewById(R.id.user_email_TextView);
        userImageView        = drawer.findViewById(R.id.user_imageView);

        userNicknameTextView.setText("");
        userEmailTextView.setText("");*/

    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {

            if (preferences.getString("userType", "").equals("Aluno")) {
                loadStudentClass(preferences);
            }else if (preferences.getString("userType", "").equals("Professor")) {
                Intent teacherIntent = new Intent(getApplicationContext(), TeacherClassesActivity.class);
                startActivity(teacherIntent);
            }

        } else if (id == R.id.nav_annotation) {

        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_exit) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, 0 );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            removeAllPreferences(editor);
            firebaseHelper.getAuth().signOut();
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadStudentClass(SharedPreferences preferences){

        if (preferences.getString("classID", "").equals("")){
            Intent intentNewClass = new Intent(getApplicationContext(), StudentClassActivity.class);
            startActivity(intentNewClass);
        }else {
            Intent intentCalendar = new Intent(getApplicationContext(), CalendarMainActivity.class);
            startActivity(intentCalendar);
        }
    }

    public void removeAllPreferences(SharedPreferences.Editor editor){

        editor.remove("userID");
        editor.remove("name");
        editor.remove("lastName");
        editor.remove("nickname");
        editor.remove("dateOfBirth");
        editor.remove("userType");
        editor.remove("classID");
        editor.remove("course");
        editor.remove("college");
        editor.apply();
        editor.commit();
    }
}

