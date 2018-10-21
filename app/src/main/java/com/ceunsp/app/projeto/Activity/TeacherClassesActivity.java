package com.ceunsp.app.projeto.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
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
import java.util.Objects;

public class TeacherClassesActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private List<CollegeClass> collegeClassList = new ArrayList<>();
    private final DatabaseReference ref = firebaseHelper.getReference();
    private final String userID = firebaseHelper.getUserID();
    private List<String> classIDList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView emptyClass;
    private ProgressBar progressBar;
    private int index;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classes);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Minhas turmas");

        recyclerView = findViewById(R.id.teacherClasses_RV);
        progressBar  = findViewById(R.id.teacherClasses_progressBar);
        emptyClass   = findViewById(R.id.emptyClass_textView);

        android.support.design.widget.FloatingActionButton searchFab = findViewById(R.id.search_classes_fab);
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearch = new Intent(getApplicationContext(), SearchClassesActivity.class);
                startActivity(intentSearch);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplication(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        openCalendar(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));
    }

    @Override
    protected void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyClass.setVisibility(View.GONE);

        DatabaseReference userRef = ref.child("Users").child(userID).child("Classes");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                collegeClassList.clear();
                classIDList.clear();

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    classIDList.add( postsnapshot.getKey());
                }

                final DatabaseReference classRef = ref.child("CollegeClass");
                classRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot collegeSnapshot : dataSnapshot.getChildren()){
                            for (DataSnapshot courseSnapshot : collegeSnapshot.getChildren()){
                                for (DataSnapshot classSnapshot : courseSnapshot.getChildren()){

                                    for (index = 0; index <= (classIDList.size()-1); index++){
                                        if (classIDList.get(index).equals(classSnapshot.getKey())){
                                            String creationDate = (String) classSnapshot.child("creationDate").getValue();
                                            String className    = (String) classSnapshot.child("className").getValue();
                                            String creator      = (String) classSnapshot.child("creator").getValue();
                                            String classID      = (String) classSnapshot.child("classID").getValue();

                                            CollegeClass collegeClass = new CollegeClass(className, creator, creationDate, classID);
                                            collegeClassList.add(collegeClass);

                                            fillRecyclerView();

                                        }
                                    }


                                }
                            }
                        }

                        progressBar.setVisibility(View.GONE);
                        if (collegeClassList.isEmpty()){
                            emptyClass.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }
    public void fillRecyclerView(){
        ClassAdapter adapter = new ClassAdapter(collegeClassList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherClassesActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }

    public void openCalendar(int position){
        CollegeClass collegeClass = collegeClassList.get(position);
        String classID   = collegeClass.getClassID();
        String className = collegeClass.getClassName();

        Intent openCalendar = new Intent(getApplicationContext(), CalendarMainActivity.class);
        openCalendar.putExtra("classID", classID);
        openCalendar.putExtra("className", className);
        startActivity(openCalendar);
    }

}
