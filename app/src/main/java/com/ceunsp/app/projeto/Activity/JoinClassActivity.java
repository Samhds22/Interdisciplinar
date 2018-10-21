package com.ceunsp.app.projeto.Activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.Helpers.UsersAdapter;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinClassActivity extends AppCompatActivity {

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final DatabaseReference ref = firebaseHelper.getReference();
    private String userType, classID, className;
    private final String userID = firebaseHelper.getUserID();
    private List<User> studentsList = new ArrayList<>();
    private List<User> teacherList = new ArrayList<>();
    private static final String PREFERENCES = "Preferences";
    private SharedPreferences preferences;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            retrieveClassData(bundle);
            getSupportActionBar().setTitle(className);
        }


        preferences = getSharedPreferences(PREFERENCES, 0);
        userType = preferences.getString("userType", "");

        final DatabaseReference studentsRef = ref.child("Users");
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    String lastName = (String) postSnapshot.child("lastName").getValue();
                    String userType = (String) postSnapshot.child("userType").getValue();
                    String name     = (String) postSnapshot.child("name").getValue();
                    User user       = new User(name, lastName, userType);
                    String classId;

                    assert userType != null;
                    if (userType.equals("Aluno")){
                        classId = (String) postSnapshot.child("Student").child("classID").getValue();

                        if ((classId != null) &&(classId.equals(classID))){
                                studentsList.add(user);

                        }

                    } else if (userType.equals("Professor")){
                        for (DataSnapshot teacherSnapshot : postSnapshot.child("Classes").getChildren()){
                           classId = teacherSnapshot.getKey();
                            if ((classId != null) &&(classId.equals(classID))) {
                                teacherList.add(user);
                            }
                        }
                    }
                }

                fillStudentRV();
                fillTeacherRV();
            }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userType.equals("Aluno")){

                    DatabaseReference studentRef = ref.child("Users").child(userID);
                    studentRef.child("Student").child("classID").setValue(classID);
                    saveInPreferences();

                } else if (userType.equals("Professor")){

                    DatabaseReference teacherRef = ref.child("Users").child(userID);
                    teacherRef.child("Classes").child(classID).setValue(className);

                }
                finish();
            }
        });
    }

    public void retrieveClassData(Bundle bundle){
        className = bundle.getString("className");
        classID   = bundle.getString("classID");
    }

    public void fillStudentRV(){
        RecyclerView recyclerView = findViewById(R.id.student_RecyclerView);
        UsersAdapter adapter = new UsersAdapter(studentsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JoinClassActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void fillTeacherRV(){
        RecyclerView recyclerView = findViewById(R.id.teacher_RecyclerView);
        UsersAdapter adapter = new UsersAdapter(teacherList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JoinClassActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void saveInPreferences(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("classID", classID);
        editor.apply();
        editor.commit();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }
}
