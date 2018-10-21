package com.ceunsp.app.projeto.Calendar.Activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.ceunsp.app.projeto.Calendar.Helper.SlidingTabLayout;
import com.ceunsp.app.projeto.Calendar.Helper.ViewPagerAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DatabaseReference;


public class MainActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private CharSequence titles[]= {"Agenda","Histórico"};
    private ViewPagerAdapter adapter;
    String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            classID = bundle.getString("classID");
        }

        int numberOfTabs = 2;

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

        if (id == R.id.action_settings) {
            showAlertDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title1);
        builder.setMessage(R.string.message1);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cleanUserClass();
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

    public void cleanUserClass(){
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
        }
    }
}
