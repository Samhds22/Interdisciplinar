package com.ceunsp.app.projeto.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.Model.Students;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewClassActivity extends AppCompatActivity {

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private EditText collegeEdit, courseEdit, classNameEdit;
    private String userID, college, course, className;
    private Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        Bundle bundle = getIntent().getExtras();

        collegeEdit   = findViewById(R.id.college_edit);
        courseEdit    = findViewById(R.id.course_edit);
        classNameEdit = findViewById(R.id.class_name_edit);
        saveButton    = findViewById(R.id.save_class_button);

        userID  = bundle.getString("UID");
        college = bundle.getString("college");
        course  = bundle.getString("course");

        collegeEdit.setText(college);
        courseEdit.setText(course);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference userRef = ref.child("Users").child(userID);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        className = classNameEdit.getText().toString();
                        String creationDate = GetDate();
                        CleanUpStrings(college, course);

                        DatabaseReference collegeClassRef = ref.child("CollegeClass").child(college).child(course);
                        DatabaseReference pushKey = collegeClassRef.push();

                        Students students = new Students(userID);
                        CollegeClass collegeClass = new CollegeClass(college, course, className, userName, creationDate, students);
                        pushKey.setValue(collegeClass);

                        DatabaseReference userQry = ref.child("Users").child(userID);
                        userQry.child("collegeClass").setValue(className);
                        userQry.child("half").setValue(1); //alterar

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    public void CleanUpStrings(String str1, String str2){
        str1    = str1.replace(" ", "");
        str2    = str2.replace(" ", "");
        college = str1;
        course  = str2;
    }

    public String GetDate(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        String date = sdf.format(calendar.getTime());

        return date;
    }
}
