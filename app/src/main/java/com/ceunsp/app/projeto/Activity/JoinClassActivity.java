package com.ceunsp.app.projeto.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.ceunsp.app.projeto.Model.CollegeClass;
import com.ceunsp.app.projeto.Model.Students;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JoinClassActivity extends AppCompatActivity {

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private String classID, college, course, userID, userName;
    private List<String> usersList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        classID = bundle.getString("classID");
        college = bundle.getString("college");
        course  = bundle.getString("course");

        LoadStudents();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void LoadStudents(){

        DatabaseReference classRef = ref.child("CollegeClass").child(college).child(course)
                .child(classID).child("students");
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    userID = (String) postSnapshot.getValue();

                    DatabaseReference userRef = ref.child("Users").child(userID);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            userName = dataSnapshot.child("name").getValue()
                                    + " " + dataSnapshot.child("lastName").getValue();
                            usersList.add(userName);

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

}
