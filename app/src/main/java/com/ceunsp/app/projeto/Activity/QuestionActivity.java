package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DatabaseReference;

public class QuestionActivity extends AppCompatActivity {

    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private static final String PREFERENCES = "Preferences";
    private String userId = firebaseHelper.getUserID();
    private Spinner collegeSpinner, courseSpinner;
    private Button proceedButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setTitle("Só mais um pouco...");

        collegeSpinner  = findViewById(R.id.college_spinner);
        courseSpinner   = findViewById(R.id.course_spinner);
        proceedButton   = findViewById(R.id.proceed_button);

        LoadSpinners();

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "";
                if (collegeSpinner.getSelectedItemId() != 0 && courseSpinner.getSelectedItemId() != 0){
                    String college = collegeSpinner.getSelectedItem().toString();
                    String course  = courseSpinner.getSelectedItem().toString();
                    saveUserData(college, course);
                    saveInPreferences(college, course);
                    nextActivity();

                }else
                    info = "Preencha os dados solicitados antes de continuar!";
                    Snackbar.make(v, info , Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        if (firebaseHelper.getAuth() == null){
            finish();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {

    }

    private void LoadSpinners() {
        String[] collegeData = getResources().getStringArray(R.array.colleges);
        ArrayAdapter<String> adapterCollege =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, collegeData);
        adapterCollege.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeSpinner.setAdapter(adapterCollege);

        String[] coursesData = getResources().getStringArray(R.array.courses);
        ArrayAdapter<String> adapterCourse =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coursesData);
        adapterCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapterCourse);
    }
    public void saveUserData(String college, String course){
        DatabaseReference userRef = firebaseHelper.getReference().child("Users").child(userId);
        userRef.child("college").setValue(college);
        userRef.child("course").setValue(course);
    }

    public void saveInPreferences(String college, String course){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("college", college);
        editor.putString("course", course);
        editor.apply();
        editor.commit();
    }

    public void nextActivity(){
        Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}
