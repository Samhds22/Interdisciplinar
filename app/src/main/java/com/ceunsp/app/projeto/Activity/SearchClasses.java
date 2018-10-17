package com.ceunsp.app.projeto.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ceunsp.app.projeto.Helpers.CollegeClassAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchClasses extends AppCompatActivity {

    private List<CollegeClass> collegeClassList = new ArrayList<CollegeClass>();
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private Spinner collegeSpinner, courseSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_classes);

        collegeSpinner = findViewById(R.id.college_spinner);
        courseSpinner  = findViewById(R.id.course_spinner);

        loadSpinners();

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String college = cleanUpStrings(collegeSpinner.getSelectedItem().toString());
                String selectedCourse = cleanUpStrings(parent.getSelectedItem().toString());

                if (!college.isEmpty()){

                    DatabaseReference ref = firebaseHelper.getReference().child("CollegeClass")
                            .child(college).child(selectedCourse);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            collegeClassList.clear();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                loadRecyclerView(postSnapshot);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinners() {
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

    public void loadRecyclerView(DataSnapshot postSnapshot){

        String creationDate = (String) postSnapshot.child("creationDate").getValue();
        String className    = (String) postSnapshot.child("className").getValue();
        String creator      = (String) postSnapshot.child("creator").getValue();
        String classID      = (String) postSnapshot.child("classID").getValue();

        CollegeClass collegeClass = new CollegeClass(className, creator, creationDate, classID);
        collegeClassList.add(collegeClass);

        RecyclerView recyclerView = findViewById(R.id.class_recyclerView);
        CollegeClassAdapter adapter = new CollegeClassAdapter(collegeClassList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchClasses.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public String cleanUpStrings(String str){
        str = str.replace(" ", "");
        return str;
    }
}
