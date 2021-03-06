package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.ceunsp.app.projeto.Helpers.ClassAdapter;
import com.ceunsp.app.projeto.Helpers.RecyclerItemClickListener;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentClassActivity extends AppCompatActivity {

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private static final String PREFERENCES = "Preferences";
    private List<CollegeClass> collegeClassList = new ArrayList<>();
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.class_recyclerView);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Escolha uma turma");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNewClass =  new Intent(getApplicationContext(), NewClassActivity.class);
                startActivity(intentNewClass);
            }
        });
    }

    @Override
    protected void onStart(){

        final ProgressBar progressBar      = findViewById(R.id.teste);
        final LinearLayout rvLayout        = findViewById(R.id.recyclerView_layout);
        final ConstraintLayout emptyLayout = findViewById(R.id.empty_layout);


        SharedPreferences preferences = getSharedPreferences(PREFERENCES, 0);

        if (!preferences.getString("classID" , "").equals("")){
            Intent intentCalendar = new Intent(getApplicationContext(), CalendarMainActivity.class);
            startActivity(intentCalendar);
            finish();
        }

        final String college = cleanUpStrings(preferences.getString("college", ""));
        final String course  = cleanUpStrings(preferences.getString("course", ""));

            Query classQry = ref.child("CollegeClass").child(college).child(course);
            classQry.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    collegeClassList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {

                            String creationDate = (String) postSnapshot.child("creationDate").getValue();
                            String className    = (String) postSnapshot.child("className").getValue();
                            String creator      = (String) postSnapshot.child("creator").getValue();
                            String classID      = (String) postSnapshot.child("classID").getValue();

                            CollegeClass collegeClass = new CollegeClass(className, creator, creationDate, classID);
                            collegeClassList.add(collegeClass);
                        }
                    }
                    if (collegeClassList.isEmpty()){
                        progressBar.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        rvLayout.setVisibility(View.VISIBLE);
                        fillRecyclerView();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
        super.onStart();
    }

    public String cleanUpStrings(String str){
        str = str.replace(" ", "");
        return str;
    }

    public void fillRecyclerView(){
        ClassAdapter adapter = new ClassAdapter(collegeClassList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(StudentClassActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
}
