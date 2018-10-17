package com.ceunsp.app.projeto.Activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ceunsp.app.projeto.Helpers.FirebaseHelper;
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private View view;
    private EditText email, password;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        email    = findViewById(R.id.email_edit);
        password = findViewById(R.id.password_edit);

        Button singIn = findViewById(R.id.sign_in_button);
        Button register = findViewById(R.id.reg_button);

        singIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view = v;
                attemptLogin();
            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistration = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegistration);
            }
        });
    }

    @Override
    protected void onStart() {
        if (auth.getCurrentUser() != null) {
           nextActivity();
        }
        super.onStart();
    }

    private void attemptLogin() {

        email.setError(null);
        password.setError(null);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(passwordText) && !isPasswordValid(passwordText)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        if (TextUtils.isEmpty(emailText)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(emailText)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            if (checkConnection()){
                ValidateUser(emailText, passwordText);
            } else{
                Toast.makeText(getApplicationContext(), "Sem conexão", Toast.LENGTH_LONG);
            }

        }
    }

    private void ValidateUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            nextActivity();

                        }else{
                            Snackbar.make(view, "Ocorreu um erro ao realizar o login",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void nextActivity(){
        String userId = auth.getCurrentUser().getUid();

        DatabaseReference userRef = firebaseHelper.getReference().child("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userType = (String) dataSnapshot.child("userType").getValue();
                String college  = (String) dataSnapshot.child("college").getValue();
                String course   = (String) dataSnapshot.child("course").getValue();


                if ((userType.equals("Aluno"))&& (college.equals("")) && (course.equals(""))){

                    Intent intentQuestion = new Intent(getApplicationContext(), QuestionActivity.class);
                    startActivity(intentQuestion);

                } else{

                    Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intentHome);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  boolean checkConnection() {
        boolean conected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conected = true;
        } else {
            conected = false;
        }
        return conected;
    }
}

