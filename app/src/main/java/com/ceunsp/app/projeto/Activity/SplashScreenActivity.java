package com.ceunsp.app.projeto.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseHelper.getAuth().getCurrentUser() == null){
                    OpenLogin();
                } else {
                    nextActivity();
                }

            }
        }, 1200);

    }
    private void OpenLogin() {

        Intent intent = new Intent(SplashScreenActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void nextActivity() {

        String userId = firebaseHelper.getAuth().getCurrentUser().getUid();

        DatabaseReference userRef = firebaseHelper.getReference().child("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = (String) dataSnapshot.child("userType").getValue();
                String college = (String) dataSnapshot.child("college").getValue();
                String course = (String) dataSnapshot.child("course").getValue();

                if ((userType.equals("Aluno")) && (college.equals("")) && (course.equals(""))){

                    Intent intentQuestion = new Intent(getApplicationContext(), QuestionActivity.class);
                    startActivity(intentQuestion);

                } else {

                    Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intentHome);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}