package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceunsp.app.projeto.Helpers.ClassAdapter;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.RecyclerItemClickListener;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchClassesActivity extends AppCompatActivity {

    private List<CollegeClass> collegeClassList = new ArrayList<>();
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private Spinner collegeSpinner, courseSpinner;
    private String college, course;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_classes);
        getSupportActionBar().setTitle("Encontre uma turma");

        collegeSpinner  = findViewById(R.id.college_spinner);
        courseSpinner   = findViewById(R.id.course_spinner);
        recyclerView    = findViewById(R.id.class_recyclerView);

        loadSpinners();

        Button searchButton = findViewById(R.id.search_classes_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collegeClassList.clear();

                final ProgressBar loadingPG = findViewById(R.id.load_class_progressBar);
                final TextView notFoundClasses    = findViewById(R.id.not_found_classes_text);

                loadingPG.setVisibility(View.VISIBLE);
                notFoundClasses.setVisibility(View.GONE);

                college = cleanUpStrings(collegeSpinner.getSelectedItem().toString());
                course = cleanUpStrings(courseSpinner.getSelectedItem().toString());

                if (collegeSpinner.getSelectedItemId() != 0 && courseSpinner.getSelectedItemId() != 0){

                    DatabaseReference ref = firebaseHelper.getReference().child("CollegeClass")
                            .child(college).child(course);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                                    fillRecyclerView(postSnapshot);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }

                                loadingPG.setVisibility(View.GONE);

                                if (collegeClassList.isEmpty()){
                                    notFoundClasses.setVisibility(View.VISIBLE);
                                }

                            } else {
                                loadingPG.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                notFoundClasses.setVisibility(View.VISIBLE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {

                    Snackbar.make(v, "Selecione a faculdade e curso para continuar!",
                            Snackbar.LENGTH_LONG).show();

                    loadingPG.setVisibility(View.GONE);
                    notFoundClasses.setVisibility(View.VISIBLE);
                }

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplication(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CollegeClass collegeClass = collegeClassList.get(position);
                        String classID   = collegeClass.getClassID();
                        String className = collegeClass.getClassName();

                        Intent intentJoin = new Intent(getApplicationContext(), JoinClassActivity.class);
                        intentJoin.putExtra("className", className);
                        intentJoin.putExtra("classID", classID);
                        startActivity(intentJoin);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));
    }

    private void loadSpinners() {
        String[] collegeData = getResources().getStringArray(R.array.colleges);
        ArrayAdapter<String> adapterCollege =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, collegeData);
        adapterCollege.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeSpinner.setAdapter(adapterCollege);

        String[] coursesData = getResources().getStringArray(R.array.courses);
        ArrayAdapter<String> adapterCourse =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coursesData);
        adapterCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapterCourse);
    }

    public void fillRecyclerView(DataSnapshot postSnapshot){

        String creationDate = (String) postSnapshot.child("creationDate").getValue();
        String creator      = (String) postSnapshot.child("creator").getValue();
        String classID      = (String) postSnapshot.child("classID").getValue();
        String className    = (String) postSnapshot.child("className").getValue();

        CollegeClass collegeClass = new CollegeClass(className, creator, creationDate, classID);
        collegeClassList.add(collegeClass);

        ClassAdapter adapter = new ClassAdapter(collegeClassList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchClassesActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public String cleanUpStrings(String str){
        str = str.replace(" ", "");
        return str;
    }
}
