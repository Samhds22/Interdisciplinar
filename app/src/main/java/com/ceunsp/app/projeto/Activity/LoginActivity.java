package com.ceunsp.app.projeto.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private EditText emailEdit, passwordEdit;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private FirebaseAuth auth = firebaseHelper.getAuth();
    private static final String PREFERENCES = "Preferences";
    private Button singInButton, registerButton;
    private SharedPreferences preferences;
    private ProgressBar progressBar;
    private ImageView logoImageView;
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar = findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);
        preferences = getSharedPreferences(PREFERENCES, 0);

        logoImageView  = findViewById(R.id.logo_ImageView);
        singInButton   = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.reg_button);
        emailEdit      = findViewById(R.id.email_edit);
        passwordEdit   = findViewById(R.id.password_edit);
        infoTextView   = findViewById(R.id.info_textView);

        singInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view = v;
                attemptLogin();
            }
        });


        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistration = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegistration);
            }
        });
    }

    @Override
    protected void onStart() {

        activeProgressBar(false);

        if (auth.getCurrentUser() != null) {
            nextActivity();
        }
        super.onStart();
    }

    private void attemptLogin() {

        emailEdit.setError(null);
        passwordEdit.setError(null);
        String emailText = emailEdit.getText().toString();
        String passwordText = passwordEdit.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(passwordText) && !isPasswordValid(passwordText)) {
            passwordEdit.setError(getString(R.string.error_invalid_password));
            focusView = passwordEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailEdit.setError(getString(R.string.error_field_required));
            focusView = emailEdit;
            cancel = true;
        } else if (!isEmailValid(emailText)) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailEdit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            if (checkConnection()){
                activeProgressBar(true);
                singIn(emailText, passwordText);
            } else{
                Toast.makeText(getApplicationContext(), "Sem conexão", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void singIn(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            DatabaseReference userRef = firebaseHelper.getReference().child("Users")
                                    .child(firebaseHelper.getUserID());

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    SharedPreferences.Editor editor = preferences.edit();
                                    saveUserPreferences(editor, dataSnapshot);
                                    saveStudentPreferences(editor ,dataSnapshot.child("Student"));
                                    nextActivity();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        }else{
                            activeProgressBar(false);
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

        if (preferences.getString("userType", "").equals("Aluno")){

            String college = preferences.getString("college", "");
            String course  = preferences.getString("course", "");

            if (college.equals("") || course.equals("")){
                openQuestionActivity();
            } else {
                Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intentHome);
            }

        } else if (preferences.getString("userType", "").equals("Professor")){
            Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intentHome);
        }
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

    public void openQuestionActivity(){
        String college = preferences.getString("college", "");
        String course  = preferences.getString("course", "");
        if (college.isEmpty() || course.isEmpty()){
            Intent intentQuestion = new Intent(getApplicationContext(), StudentQuestionActivity.class);
            startActivity(intentQuestion);
        } else{
            Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intentHome);
        }
    }
    public void activeProgressBar(boolean answer){
        if (answer) {
            logoImageView.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            singInButton.setVisibility(View.GONE);
            emailEdit.setVisibility(View.GONE);
            passwordEdit.setVisibility(View.GONE);
            infoTextView.setText(R.string.wait);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            logoImageView.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            singInButton.setVisibility(View.VISIBLE);
            emailEdit.setVisibility(View.VISIBLE);
            passwordEdit.setVisibility(View.VISIBLE);
            infoTextView.setText(R.string.not_have_Account);
        }
    }

    public void saveUserPreferences(SharedPreferences.Editor editor, DataSnapshot dataSnapshot){

        editor.putString("name", (String) dataSnapshot.child("name").getValue());
        editor.putString("lastName", (String) dataSnapshot.child("lastName").getValue());
        editor.putString("nickname", (String) dataSnapshot.child("nickname").getValue());
        editor.putString("dateOfBirth", (String) dataSnapshot.child("dateOfBirth").getValue());
        editor.putString("userType", (String) dataSnapshot.child("userType").getValue());
        editor.putString("email", auth.getCurrentUser().getEmail());
        editor.apply();
        editor.commit();
    }

    public void saveStudentPreferences(SharedPreferences.Editor editor, DataSnapshot dataSnapshot){

        editor.putString("classID", (String) dataSnapshot.child("classID").getValue());
        editor.putString("college", (String) dataSnapshot.child("college").getValue());
        editor.putString("course",  (String) dataSnapshot.child("course").getValue());
        editor.apply();
        editor.commit();
    }
}

