package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceunsp.app.projeto.Adapter.CollegeClassAdapter;
import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CollegeClassActivity extends AppCompatActivity {

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID = auth.getCurrentUser().getUid();
    List<CollegeClass> collegeClassList = new ArrayList<CollegeClass>();
    private Query collegeClassQry;
    //private ListView listView;
    private String college, course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Escolha uma turma");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNewClass =  new Intent(getApplicationContext(), NewClassActivity.class);
                intentNewClass.putExtra("UID", userID);
                intentNewClass.putExtra("college", college);
                intentNewClass.putExtra("course", course);
                startActivity(intentNewClass);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listView = findViewById(R.id.college_class_list);

        Query userQry = ref.child("Users").child(userID);
        userQry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    college = dataSnapshot.child("college").getValue().toString();
                    course  = dataSnapshot.child("course").getValue().toString();


                    String collegeAux = CleanUpStrings(college);
                    String courseAux   = CleanUpStrings(course);

                    collegeClassQry = ref.child("CollegeClass").child(collegeAux).child(courseAux);
                    collegeClassQry.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            collegeClassList.clear();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                if (dataSnapshot.exists()){
                                    String className = postSnapshot.child("className").getValue().toString();
                                    String creator = postSnapshot.child("creator").getValue().toString();
                                    String creationDate = postSnapshot.child("creationDate").getValue().toString();

                                    CollegeClass collegeClass = new CollegeClass(className, creator, creationDate);
                                    collegeClassList.add(collegeClass);

                                    RecyclerView recyclerView = findViewById(R.id.class_recyclerView);
                                    CollegeClassAdapter adapter = new CollegeClassAdapter(collegeClassList);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CollegeClassActivity.this);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public String CleanUpStrings(String str){
        str = str.replace(" ", "");
        return str;
    }

}
