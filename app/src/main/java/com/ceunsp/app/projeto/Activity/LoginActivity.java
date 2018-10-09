package com.ceunsp.app.projeto.Activity;


import android.content.Intent;
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
import com.ceunsp.app.projeto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private View view;
    private EditText email, password;
    private FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        email = findViewById(R.id.email_edit);
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
        if (user.getCurrentUser() != null) {
            Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intentHome);
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

        // Verifica se a senha é valida
        if (!TextUtils.isEmpty(passwordText) && !isPasswordValid(passwordText)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Verifica se é um email válido.
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
            ValidateUser(emailText, passwordText);
        }
    }

    private void ValidateUser(String email, String password){
        user.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intentHome);
                        }else{
                            Snackbar.make(view, "Ocorreu um erro ao realizar o login", Snackbar.LENGTH_LONG)
                                        .show();
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

}

