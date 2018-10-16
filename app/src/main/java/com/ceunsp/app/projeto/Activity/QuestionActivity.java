package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
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
    private String userId = firebaseHelper.getUserID();
    private Spinner collegeSpinner, courseSpinner;
    private String college, course;
    private Button proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                college = collegeSpinner.getSelectedItem().toString();
                course  = courseSpinner.getSelectedItem().toString();
                String info = "";

                if (collegeSpinner.getSelectedItemId() != 0 && courseSpinner.getSelectedItemId() != 0){
                    saveUserData();
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
    public void saveUserData(){
        DatabaseReference userRef = firebaseHelper.getReference().child("Users").child(userId);
        userRef.child("college").setValue(college);
        userRef.child("course").setValue(course);
    }
    public void nextActivity(){
        Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentHome);
    }
}
