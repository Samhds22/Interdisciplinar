package com.ceunsp.app.projeto.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.ceunsp.app.projeto.Calendar.Activity.MainActivity;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Model.TeacherClasses;
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    final StorageReference storage = firebaseHelper.getStorage();
    private static final String PREFERENCES = "Preferences";
    final String userID = firebaseHelper.getUserID();
    public TextView userNicknameTextView, userEmailTextView;
    public ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userNicknameTextView = findViewById(R.id.user_nickname_TextView);
        userEmailTextView    = findViewById(R.id.user_email_TextView);
        userImageView        = findViewById(R.id.user_imageView);

        loadUserDrawer();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            if (CheckConnection()){

                DatabaseReference userRef = firebaseHelper.getReference().child("Users").child(userID);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            String userType = (String) dataSnapshot.child("userType").getValue();

                            if (userType.equals("Aluno")){
                                loadStudentClass(dataSnapshot, userType);

                            } else if (userType.equals("Professor")){

                                Intent teacherIntent;
                                teacherIntent = new Intent(getApplicationContext(),
                                        TeacherClassesActivity.class);
                                startActivity(teacherIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        } else {
            Toast.makeText(getApplicationContext(),"Sem conexão", Toast.LENGTH_LONG).show();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  boolean CheckConnection() {
        boolean conected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conected = true;
        } else {
            conected = false;
        }
        return conected;
    }

    public void loadStudentClass(DataSnapshot dataSnapshot, String userType){

        String collegeClassID = (String) dataSnapshot.child("collegeClassID").getValue();
        if (collegeClassID.equals("")){
            Intent intentNewClass = new Intent(getApplicationContext(), CollegeClassActivity.class);
            startActivity(intentNewClass);
        }else {
            Intent intentCalendar = new Intent(getApplicationContext(), MainActivity.class);
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
        editor.apply();
        editor.commit();
    }

    public void loadUserDrawer(){

    }
}

