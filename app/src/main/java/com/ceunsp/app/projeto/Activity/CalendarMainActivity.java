package com.ceunsp.app.projeto.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.ceunsp.app.projeto.Helpers.SlidingTabLayout;
import com.ceunsp.app.projeto.Helpers.ViewPagerAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;


public class CalendarMainActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    String classID;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        CharSequence titles[]= {"Agenda","Atualizações"};

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            classID = bundle.getString("classID");
        }

        int numberOfTabs = 2;

        ViewPagerAdapter adapter;
        if (classID != null){
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, numberOfTabs, classID);
        } else {
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, numberOfTabs);
        }

        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        SlidingTabLayout tabs = findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.calendar_background);
            }
        });

        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_exitClass) {
            confirmDelete();
            super.onOptionsItemSelected(item);

        } else if (id == android.R.id.home){
            finish();

        } else if (id == R.id.action_people) {
            Intent intent = new Intent(getApplicationContext(), JoinClassActivity.class);
            intent.putExtra("classID", classID);
            intent.putExtra("operation", "view");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarMainActivity.this);
        builder.setTitle(R.string.title1);
        builder.setMessage(R.string.message1);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteClass();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                closeContextMenu();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteClass(){
        final String PREFERENCES = "Preferences";
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        String userType = preferences.getString("userType", "");
        String userID = firebaseHelper.getUserID();

        DatabaseReference userRef;

        if (userType.equals("Aluno")){

            userRef = firebaseHelper.getReference().child("Users").child(userID)
                    .child("Student").child("classID");
            userRef.setValue("");
            editor.remove("classID");
            editor.apply();
            editor.commit();
            finish();

        } else if (userType.equals("Professor")){

            FirebaseHelper firebaseHelper = new FirebaseHelper();
            DatabaseReference ref = firebaseHelper.getReference();
            DatabaseReference teacherRef = ref.child("Users").child(userID);
            teacherRef.child("Classes").child(classID).setValue(null);
            finish();
        }
    }

}
