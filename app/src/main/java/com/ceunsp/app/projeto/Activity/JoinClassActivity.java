package com.ceunsp.app.projeto.Activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ceunsp.app.projeto.Model.Historic;
import com.ceunsp.app.projeto.Model.User;
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinClassActivity extends AppCompatActivity {

    private CircleImageView defaultProfile;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final DatabaseReference ref = firebaseHelper.getReference();
    private String userType, classID, className, operation;
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

        defaultProfile = findViewById(R.id.joinClas_defaultProfile);
        preferences    = getSharedPreferences(PREFERENCES, 0);
        userType       = preferences.getString("userType", "");


        final DatabaseReference studentsRef = ref.child("Users");
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    String userID   =  postSnapshot.getKey();
                    String lastName = (String) postSnapshot.child("lastName").getValue();
                    String userType = (String) postSnapshot.child("userType").getValue();
                    String name     = (String) postSnapshot.child("name").getValue();

                    final User user = new User(name, lastName, userType);
                    String classId;

                    assert userType != null;
                    if (userType.equals("Aluno")){
                        classId = (String) postSnapshot.child("Student").child("classID").getValue();

                        if ((classId != null) &&(classId.equals(classID))){
                           loadStudents(user, userID);
                        }

                    } else if (userType.equals("Professor")){

                        for (DataSnapshot teacherSnapshot : postSnapshot.child("Classes").getChildren()){
                           classId = teacherSnapshot.getKey();
                            if ((classId != null) &&(classId.equals(classID))) {
                                loadTeachers(user, userID);
                            }
                        }
                    }
                }
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

                createHistoric("enterTheClass");
                finish();
            }
        });

        if (operation != null && operation.equals("view")){
            fab.setVisibility(View.GONE);
        }
    }

    public void loadStudents(final User user, String userID){

        StorageReference storageRef = firebaseHelper.getStorage();
        final long ONE_MEGABYTE = 1024 * 1024;
        final Bitmap[] bitmap = new Bitmap[1];

        storageRef.child("profilePicture."+ userID).getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>(){
                    @Override
                    public void onSuccess(byte[] bytes) {

                        bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        user.setImgProfile(bitmap[0]);
                        studentsList.add(0, user);
                        fillStudentRV();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                bitmap[0] = retrieveDefaultImage();
                user.setImgProfile(bitmap[0]);
                studentsList.add(0, user);
                fillStudentRV();
            }
        });
    }

    public void loadTeachers(final User user, String userID){

        StorageReference storageRef = firebaseHelper.getStorage();
        final long ONE_MEGABYTE = 1024 * 1024;
        final Bitmap[] bitmap = new Bitmap[1];

        storageRef.child("profilePicture."+ userID).getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>(){
                    @Override
                    public void onSuccess(byte[] bytes) {

                        bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        user.setImgProfile(bitmap[0]);
                        teacherList.add(0, user);
                        fillTeacherRV();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                bitmap[0] = retrieveDefaultImage();
                user.setImgProfile(bitmap[0]);
                teacherList.add(0, user);
                fillTeacherRV();
            }
        });

    }

    public Bitmap retrieveDefaultImage(){
        defaultProfile.setImageResource(R.drawable.default_profile_image);
        defaultProfile.setDrawingCacheEnabled(true);
        defaultProfile.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        defaultProfile.layout(0, 0, defaultProfile.getMeasuredWidth(), defaultProfile.getMeasuredHeight());
        defaultProfile.buildDrawingCache();
        return Bitmap.createBitmap(defaultProfile.getDrawingCache());
    }

    public void retrieveClassData(Bundle bundle){
        className = bundle.getString("className");
        classID   = bundle.getString("classID");
        operation = bundle.getString("operation");
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

    public void createHistoric(String action){

        DatabaseReference ref = firebaseHelper.getReference().child("Historic");
        DatabaseReference historicRef = ref.child(classID).push();
        Calendar calendarAux = Calendar.getInstance();

        String name     = preferences.getString("name", "");
        String lastName = preferences.getString("lastName", "");
        String userType = preferences.getString("userType", "");
        String date     = convertDate(calendarAux.getTime());
        String hour     = convertHour(calendarAux.getTime());
        String fullName = name + " " + lastName;

        Historic historic = new Historic(fullName, userType, userID,
                action, date, hour);

        historicRef.setValue(historic);
    }

    public String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
        return sdf.format(date);
    }

    public String convertHour(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("pt","BR"));
        return sdf.format(date);
    }
}
